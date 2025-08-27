package Main;

import Attendance.AttModel;
import Faculty.FacultyModel;
import Fingerprint.AttendanceThread;
import Fingerprint.FingerprintModel;
import Main.Views.*;
import Login.*;
import Student.StudentModel;
import com.digitalpersona.uareu.UareUException;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.Image;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class MainSerImpl implements MainService {

    MainFrame frame;
    MainPanel panel;
    MainDAO dao = new MainDAOImpl();
    private String scheduleID;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean scanningActive = false;
    private Map<String, StudentModel> studentMap;

    private String testTime = "13:00:00";

    public MainSerImpl(MainFrame frame, MainPanel panel) {
        this.frame = frame;
        this.panel = panel;

        loadSchedulesForToday();

//        startScheduleChecker();
    }

    @Override
    public void loginButton() {
        LoginFrame lgnfrm = new LoginFrame();
        LoginFrameFPrint lgnfrmFP = new LoginFrameFPrint();
        new LoginController(lgnfrm, lgnfrmFP);
//        lgnfrm.setVisible(true);
        lgnfrmFP.setVisible(true);
        frame.setVisible(false);
    }

    @Override
    public void checkAndLoadStudents() {
//        LocalTime now = LocalTime.now();
        LocalTime now = LocalTime.parse(testTime);
        System.out.println("Time: " + now);
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

        for (int row = 0; row < frame.scheduleTable.getRowCount(); row++) {
            try {
                String scheduleId = frame.scheduleTable.getValueAt(row, 0).toString();
                String subject = frame.scheduleTable.getValueAt(row, 1).toString();
                String startTimeStr = frame.scheduleTable.getValueAt(row, 3).toString();
                String endTimeStr = frame.scheduleTable.getValueAt(row, 4).toString();
                String faculty = frame.scheduleTable.getValueAt(row, 5).toString();

                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

                if (!now.isBefore(startTime) && !now.isAfter(endTime)) {

                    scheduleID = scheduleId;
                    frame.subject.setText(subject);
                    frame.startTime.setText(startTimeStr);
                    frame.endTime.setText(endTimeStr);
                    frame.faculty.setText(faculty);

                    studentMap = preloadClassStudents(scheduleId);
//                    DefaultTableModel studentsModel = dao.fetchStudentsBySchedule(scheduleId);
//                    frame.jTable1.setModel(studentsModel);

                    startScanLoop();
                    System.out.println("Loaded students for schedule: " + scheduleId);
                    break;
                }

            } catch (Exception e) {
                System.err.println("Error parsing time for row " + row + ": " + e.getMessage());
            }
        }

    }

    @Override
    public void checkAndVerifyStudents() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(true);

        AttendanceThread attendanceThread = new AttendanceThread(progressBar);

        Thread thread = new Thread(() -> {
            attendanceThread.onFingerprintCaptured(); // starts capture loop
        });
        thread.start();

        try {
            attendanceThread.startAttendance(); // blocks until identification is done
        } catch (InterruptedException | UareUException e) {
            e.printStackTrace();
        }

        FingerprintModel user = attendanceThread.getIdentifiedUser();
        if (user != null) {
            String role = user.getRole();
            String userId = user.getUser_id();
            String fname = user.getFname();
            String lname = user.getLname();

            System.out.println("role: " + role);
            if (role.equalsIgnoreCase("student")) {
                StudentModel matchedStudent = studentMap.get(userId);
                if (matchedStudent == null || matchedStudent.getStud_id() == null) {
                    JOptionPane.showMessageDialog(null,
                            "Student with ID " + userId + " is NOT in this class schedule.");
                    return;
                }
                System.out.println("student here");
                addInTable(userId, fname, lname);

                handleMatchedStudent(userId);
            } else if (role.equalsIgnoreCase("faculty")) {
                FacultyModel faculty = dao.getAssignedFacultyInfo(scheduleID, userId);

                if (faculty == null) {
                    JOptionPane.showMessageDialog(null, "Instructor mismatch: not assigned to this class.", "Attendance", JOptionPane.WARNING_MESSAGE);
                }
                System.out.println("faculty here");
                addInTable(userId, fname, lname);
                handleMatchedFaculty(userId);
            }

        } else {
            System.out.println("No user identified.");
        }

        attendanceThread.shutdown();
    }

    private int getStudentRowIndex(String studentId) {
        DefaultTableModel model = (DefaultTableModel) frame.studentTable.getModel();
        int rowCount = model.getRowCount();

        for (int i = 0; i < rowCount; i++) {
            Object cell = model.getValueAt(i, 0); // Column 0 = userId
            if (cell == null) {
                System.out.println("safeGetValueAt: Null value at row " + i + ", column 0");
                continue;
            }

            String cellValue = cell.toString().trim();
            if (cellValue.equals(studentId)) {
                return i;
            }
        }
        return -1;

    }

    private void addInTable(String userId, String fname, String lname) {
        int rowIndex = getStudentRowIndex(userId);
        DefaultTableModel model = (DefaultTableModel) frame.studentTable.getModel();

        SwingUtilities.invokeLater(() -> {
            if (rowIndex == -1) {
                model.addRow(new Object[]{
                    userId,
                    fname,
                    lname,
                    "", // Time In
                    "" // Time Out
                });

            }
            // Don't clear the table!
        });
    }

    private void startScheduleChecker() {
        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
//                LocalTime now = LocalTime.now();
                LocalTime now = LocalTime.parse(testTime);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                for (int row = 0; row < frame.scheduleTable.getRowCount(); row++) {
                    String startTimeStr = frame.scheduleTable.getValueAt(row, 3).toString();
                    String endTimeStr = frame.scheduleTable.getValueAt(row, 4).toString();
                    String scheduleId = frame.scheduleTable.getValueAt(row, 0).toString();

                    try {
                        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
                        LocalTime graceEndTime = endTime.plusMinutes(15);

                        if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                            checkAndLoadStudents();
                        }
                        if (now.isAfter(graceEndTime)) {
                            DefaultTableModel model = (DefaultTableModel) frame.studentTable.getModel();

                            for (int i = 0; i < model.getRowCount(); i++) {
                                String userId = model.getValueAt(i, 0).toString();
                                String timeInStr = model.getValueAt(i, 3).toString();
                                String timeOutStr = model.getValueAt(i, 4).toString();

                                boolean hasTimeIn = timeInStr != null && !timeInStr.trim().isEmpty();
                                boolean hasNoTimeOut = timeOutStr == null || timeOutStr.trim().isEmpty();

                                if (hasTimeIn && hasNoTimeOut) {
                                    LocalTime timeIn = LocalTime.parse(timeInStr, timeFormatter);

                                    dao.markIncompete(userId, scheduleId, timeIn);
                                } else if (!hasTimeIn && hasNoTimeOut) {
                                    dao.markAbsent(scheduleId);
                                    System.out.println("Grace period ended. Marking absentees.");
                                }
                            }

                            // stopScanLoop(); 
                        } else {
                            System.out.println("Class ended, but still within grace period for time-out.");
                            // continue waiting for time-out scans
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 0, 60 * 1000); // every 1 min
    }

    @Override
    public void loadSchedulesForToday() {
        DefaultTableModel model = dao.fetchSchedulesForToday();
        frame.scheduleTable.setModel(model);
        checkAndLoadStudents();
    }

    private void startScanLoop() {
        if (scanningActive) {
            System.out.println("Scan loop already running.");
            return;
        }

        scanningActive = true;
        scanExecutor.submit(() -> {
            System.out.println("Started fingerprint scan loop.");
            while (scanningActive) {
//                checkThreadsActivity();
                checkAndVerifyStudents(); // runs async, already non-blocking
                try {
                    Thread.sleep(5000); // 5-second interval, adjust as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
            System.out.println("Scan loop ended.");
        });

    }

    private void stopScanLoop() {
//        if (scanTimer != null && scanTimer.isRunning()) {
//            scanTimer.stop();
//            System.out.println("Stopped fingerprint scan loop.");
//        }
        if (!scanningActive) {
            return;
        }
        scanningActive = false;

    }

    private void handleMatchedStudent(String userId) {
        SwingUtilities.invokeLater(() -> {

//            System.out.println("Full Name: " + fname + " " + lname);
            int rowIndex = getStudentRowIndex(userId);
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null,
                        "Student " + userId + " is enrolled but not found in the table.\n"
                        + "Please refresh the class roster or check for mismatches.");
                return;
            }

            String fname = frame.studentTable.getValueAt(rowIndex, 1).toString();
            String lname = frame.studentTable.getValueAt(rowIndex, 2).toString();
            String timeInRow = frame.studentTable.getValueAt(rowIndex, 3).toString();
            String scheduledStartTime = frame.startTime.getText();
            String scheduledEndTime = frame.endTime.getText();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String timeTest = "13:00:00";
            LocalTime now = LocalTime.parse(timeTest);
            System.out.println("scanTime Test: " + now);
//            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(scheduledStartTime, timeFormatter);
            LocalTime endTime = LocalTime.parse(scheduledEndTime, timeFormatter);
            LocalTime graceTime = endTime.plusMinutes(15);

            long classDuration = Duration.between(startTime, endTime).toMinutes();
            long currentDuration = Duration.between(startTime, now).toMinutes();

            DefaultTableModel model = (DefaultTableModel) frame.studentTable.getModel();

            if (now.isAfter(graceTime)) {
                JOptionPane.showMessageDialog(null,
                        "Student " + fname + " " + lname + " is beyond the allowed scan time.\nAttendance not recorded.");
                model.removeRow(rowIndex);
                return;
            }

            if (!timeInRow.isEmpty()) {
                System.out.println("Student " + fname + " " + lname + " has already timed in.");
//                JOptionPane.showMessageDialog(null,
//                        "Student " + fname + " " + lname + " has already timed in.");
            } else {
                String timeIn = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
                frame.studentTable.setValueAt(timeIn, rowIndex, 3);
                JOptionPane.showMessageDialog(null,
                        "Student " + fname + " " + lname + " is in current class.\nAttendance marked.");
                showStudentInfo(userId);
//                String status = now.isAfter(startTime.plusMinutes(15)) ? "Late" : "Present";
//                dao.saveAttendance(studentId, scheduleID);
            }
            String timeOutRow = frame.studentTable.getValueAt(rowIndex, 4).toString();

            if (!timeOutRow.isEmpty()) {
//                JOptionPane.showMessageDialog(null, "Student already timed out.");
                System.out.println("Student already timed out.");
                return;
            }
            if (!timeInRow.isEmpty() && timeOutRow.isEmpty()) {
                String timeOut = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
                frame.studentTable.setValueAt(timeOut, rowIndex, 4);
                saveAttendance();
//            } else if (currentDuration < classDuration) {
//                String timeOut = now.format(DateTimeFormatter.ofPattern("hh: mm a"));
//                frame.jTable2.setValueAt(timeOut, rowIndex, 4);
            }
        });
    }

    @Override
    public Map<String, StudentModel> preloadClassStudents(String scheduleCode) {
        List<StudentModel> students = dao.fetchStudentsBySchedule(scheduleID);
        Map<String, StudentModel> studentMap = new HashMap<>();

        for (StudentModel student : students) {
            if (student != null && student.getStud_id() != null) {
                studentMap.put(student.getStud_id(), student);
            }
        }

        return studentMap;
    }

    private void saveAttendance() {
        for (int row = 0; row < frame.studentTable.getRowCount(); row++) {
            String scheduleId = scheduleID;
            String userId = frame.studentTable.getValueAt(row, 0).toString();
            String timeInStr = frame.studentTable.getValueAt(row, 3).toString();
            String timeOutStr = frame.studentTable.getValueAt(row, 4).toString();
            String startTimeStr = frame.startTime.getText().trim();
            String endTimeStr = frame.endTime.getText().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

            try {
                LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
                LocalTime graceEndTime = endTime.plusMinutes(15);
                LocalTime late = startTime.plusMinutes(15);

                LocalTime timeIn = timeInStr.isEmpty() ? null : LocalTime.parse(timeInStr, formatter);
                LocalTime timeOut = timeOutStr.isEmpty() ? null : LocalTime.parse(timeOutStr, formatter);

                String status;

                if (timeIn.isBefore(late) && !timeOut.isBefore(graceEndTime)) {
                    status = "Complete";
                } else if (timeIn.isAfter(late) && timeOut.isBefore(graceEndTime)) {
                    status = "Late";
                } else if (timeOut.isBefore(endTime)) {
                    status = "Early Time Out";
                } else if (timeIn.isAfter(graceEndTime)) {
                    status = "Absent";
                } else {
                    status = "Incomplete";
                }

                AttModel att = new AttModel();
                att.setCs_id(scheduleId);
                att.setStud_id(userId);
                att.setStatus(status);
                att.setTimeIn(timeIn);
                att.setTimeOut(timeOut);

                boolean saved = dao.saveAttendance(att);
                if (saved) {
                    JOptionPane.showMessageDialog(null, "Attendance recorded for: " + userId,
                            "Attendance", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, userId + "has already attended class, attendance is already recorded.", "Attendance", JOptionPane.WARNING_MESSAGE);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showStudentInfo(String studentID) {
        System.out.println("showStudentInfo called with ID: " + studentID);
        List<AttModel> history = dao.getAttendanceHistory(studentID);
        StudentModel student = dao.fetchStudentInfo(studentID);
        System.out.println("Fetched student: " + student);
        System.out.println("History size: " + history.size());

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No attendance history found for this student.");
        }

        if (student == null) {
            JOptionPane.showMessageDialog(frame, "Student information is missing.");
        }

        panel.fname.setText(student.getFname());

        String initial = (student.getMname() != null && !student.getMname().isEmpty())
                ? student.getMname().substring(0, 1).toUpperCase() + "."
                : "";
        panel.mi.setText(initial);

        panel.lname.setText(student.getLname());
        panel.college.setText(student.getCollege());
        panel.section.setText(student.getSection());
        panel.track.setText(student.getTrack());
        panel.year.setText(student.getYear());

        System.out.println("student name: " + student.getFname() + " " + student.getLname());

        byte[] fingerprintBytes = student.getFingerprintImage();
        if (fingerprintBytes != null && fingerprintBytes.length > 0) {
            ImageIcon icon = new ImageIcon(fingerprintBytes);
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            panel.image.setIcon(new ImageIcon(scaled));
        } else {
            panel.image.setText("No image available");
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Subject", "Date", "Time In", "Time Out", "Status"
        }, 0);
        for (AttModel r : history) {
            model.addRow(new Object[]{
                r.getSubject(),
                r.getAttDateTime(),
                r.getTimeIn(),
                r.getTimeOut(),
                r.getStatus()
            });
        }
        panel.jTable1.setModel(model);

        panel.jPanel3.removeAll();
        panel.jPanel4.removeAll();
        panel.jPanel3.setLayout(new GridLayout(1, 1));
        panel.jPanel4.setLayout(new GridLayout(1, 1));

        // Pie Chart
        Map<String, Integer> statusCounts = dao.getStatusCounts(studentID);
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        statusCounts.forEach(pieDataset::setValue);
        JFreeChart pieChart = ChartFactory.createPieChart("Status Breakdown", pieDataset, true, true, false);
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        panel.jPanel3.add(pieChartPanel);

        // Bar Chart
        Map<String, Integer> subjectCounts = dao.getSubjectAttendanceCounts(studentID);
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        subjectCounts.forEach((subject, count) -> barDataset.addValue(count, "Attendance", subject));
        JFreeChart barChart = ChartFactory.createBarChart("Attendance per Subject", "Subject", "Count", barDataset);
        ChartPanel barChartPanel = new ChartPanel(barChart);
        panel.jPanel4.add(barChartPanel);

        panel.jPanel4.revalidate();
        panel.jPanel4.repaint();
        panel.jPanel3.revalidate();
        panel.jPanel3.repaint();
        panel.revalidate();
        panel.repaint();

        CardLayout cl = (CardLayout) frame.jPanel1.getLayout();
        frame.jPanel1.add(panel, "showInfo");
        cl.show(frame.jPanel1, "showInfo");
        new Timer(2500, e -> {
            byte[] profileBytes = student.getImage();
            if (profileBytes != null && profileBytes.length > 0) {
                ImageIcon icon = new ImageIcon(profileBytes);
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                panel.image.setIcon(new ImageIcon(scaled));
            } else {
                panel.image.setText("No image available");
            }
        }).start();

        new Timer(5000, e -> {
            DisplayPanel dp = new DisplayPanel();
            frame.jPanel1.add(dp, "displayPanel");
            cl.show(frame.jPanel1, "displayPanel");
// new panel here the display panel
            ((Timer) e.getSource()).stop();
        }).start();
    }

    private void handleMatchedFaculty(String userId) {
        SwingUtilities.invokeLater(() -> {
            int rowIndex = getStudentRowIndex(userId);
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null,
                        "Faculty " + userId + " is enrolled but not found in the table.\n"
                        + "Please refresh the class roster or check for mismatches.");
                return;
            }

            String fname = frame.studentTable.getValueAt(rowIndex, 1).toString();
            String lname = frame.studentTable.getValueAt(rowIndex, 2).toString();
            String timeInRow = frame.studentTable.getValueAt(rowIndex, 3).toString();
            String scheduledStartTime = frame.startTime.getText();
            String scheduledEndTime = frame.endTime.getText();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            String timeTest = "13:59:00";
            LocalTime now = LocalTime.parse(timeTest);
            System.out.println("scanTime Test: " + now);
//            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(scheduledStartTime, timeFormatter);
            LocalTime endTime = LocalTime.parse(scheduledEndTime, timeFormatter);
            LocalTime graceTime = endTime.plusMinutes(15);

            long classDuration = Duration.between(startTime, endTime).toMinutes();
            long currentDuration = Duration.between(startTime, now).toMinutes();

            if (now.isAfter(graceTime)) {
                JOptionPane.showMessageDialog(null,
                        "Faculty " + fname + " " + lname + " is beyond the allowed scan time.\nAttendance not recorded.");
                return;
            }

            if (!timeInRow.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Faculty " + fname + " " + lname + " has already timed in.");
            } else {
                String timeIn = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
                frame.studentTable.setValueAt(timeIn, rowIndex, 3);
                JOptionPane.showMessageDialog(null,
                        "Student " + fname + " " + lname + " is in current class.\nAttendance marked.");
                showFacultyInfo(userId);

            }
            String timeOutRow = frame.studentTable.getValueAt(rowIndex, 4).toString();

            if (!timeOutRow.isEmpty()) {
                System.out.println("Faculty already timed out.");
                return;
            }
            if (!timeInRow.isEmpty() && timeOutRow.isEmpty()) {
                String timeOut = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
                frame.studentTable.setValueAt(timeOut, rowIndex, 4);
                saveAttendance();
            }
        });
    }

    private void showFacultyInfo(String facultyID) {
        System.out.println("showStudentInfo called with ID: " + facultyID);
        List<AttModel> history = dao.getAttendanceHistory(facultyID);
        FacultyModel faculty = dao.getAssignedFacultyInfo(scheduleID, facultyID);
        System.out.println("Fetched student: " + faculty);
        System.out.println("History size: " + history.size());

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No attendance history found for this student.");
        }

        if (faculty == null) {
            JOptionPane.showMessageDialog(frame, "Student information is missing.");
        }

        panel.fname.setText(faculty.getFname());

        String initial = (faculty.getMname() != null && !faculty.getMname().isEmpty())
                ? faculty.getMname().substring(0, 1).toUpperCase() + "."
                : "";
        panel.mi.setText(initial);

        panel.lname.setText(faculty.getLname());
        panel.college.setText(faculty.getCollege());
        panel.section.setVisible(false);
        panel.track.setVisible(false);
        panel.year.setVisible(false);

        System.out.println("student name: " + faculty.getFname() + " " + faculty.getLname());

        byte[] fingerprintBytes = faculty.getFingerprintImage();
        if (fingerprintBytes != null && fingerprintBytes.length > 0) {
            ImageIcon icon = new ImageIcon(fingerprintBytes);
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            panel.image.setIcon(new ImageIcon(scaled));
        } else {
            panel.image.setText("No image available");
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Subject", "Date", "Time In", "Time Out", "Status"
        }, 0);
        for (AttModel r : history) {
            model.addRow(new Object[]{
                r.getSubject(),
                r.getAttDateTime(),
                r.getTimeIn(),
                r.getTimeOut(),
                r.getStatus()
            });
        }
        panel.jTable1.setModel(model);

        panel.jPanel3.removeAll();
        panel.jPanel4.removeAll();
        panel.jPanel3.setLayout(new GridLayout(1, 1));
        panel.jPanel4.setLayout(new GridLayout(1, 1));

        // Pie Chart
        Map<String, Integer> statusCounts = dao.getStatusCounts(facultyID);
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        statusCounts.forEach(pieDataset::setValue);
        JFreeChart pieChart = ChartFactory.createPieChart("Status Breakdown", pieDataset, true, true, false);
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        panel.jPanel3.add(pieChartPanel);

        // Bar Chart
        Map<String, Map<String, Integer>> attendanceMap = dao.getSectionAttendanceCounts(facultyID);
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (Map.Entry<String, Map<String, Integer>> subjectEntry : attendanceMap.entrySet()) {
            String subject = subjectEntry.getKey();
            Map<String, Integer> sectionMap = subjectEntry.getValue();

            for (Map.Entry<String, Integer> sectionEntry : sectionMap.entrySet()) {
                String section = sectionEntry.getKey();
                int count = sectionEntry.getValue();

                barDataset.addValue(count, section, subject); // (value, rowKey = section, columnKey = subject)
            }
        }

        JFreeChart barChart = ChartFactory.createBarChart("Attendance by Section per Subject",
                "Subject",
                "Attendance Count",
                barDataset,
                PlotOrientation.VERTICAL,
                //                true, true, false
                true, true, true // tooltips enabled

        );
//        CategoryPlot plot = barChart.getCategoryPlot();
//        BarRenderer renderer = (BarRenderer) plot.getRenderer();
//        renderer.setSeriesPaint(0, Color.BLUE);
        ChartPanel barChartPanel = new ChartPanel(barChart);
        panel.jPanel4.add(barChartPanel);

        panel.jPanel4.revalidate();
        panel.jPanel4.repaint();
        panel.jPanel3.revalidate();
        panel.jPanel3.repaint();
        panel.revalidate();
        panel.repaint();

        CardLayout cl = (CardLayout) frame.jPanel1.getLayout();
        frame.jPanel1.add(panel, "showInfo");
        cl.show(frame.jPanel1, "showInfo");
        new Timer(2500, e -> {
            byte[] profileBytes = faculty.getImage();
            if (profileBytes != null && profileBytes.length > 0) {
                ImageIcon icon = new ImageIcon(profileBytes);
                Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                panel.image.setIcon(new ImageIcon(scaled));
            } else {
                panel.image.setText("No image available");
            }
        }).start();

        new Timer(5000, e -> {
            DisplayPanel dp = new DisplayPanel();
            frame.jPanel1.add(dp, "displayPanel");
            cl.show(frame.jPanel1, "displayPanel");
// new panel here the display panel
            ((Timer) e.getSource()).stop();
        }).start();
    }

}
