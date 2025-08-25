package Main;

import Attendance.AttModel;
import Fingerprint.AttendanceThread;
import Fingerprint.FingerprintModel;
import Fingerprint.Selection;
import Main.Views.*;
import Login.*;
import Student.StudentModel;
import com.digitalpersona.uareu.UareUException;
import java.awt.CardLayout;
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

        for (int row = 0; row < frame.jTable1.getRowCount(); row++) {
            try {
                String scheduleId = frame.jTable1.getValueAt(row, 0).toString();
                String subject = frame.jTable1.getValueAt(row, 1).toString();
                String startTimeStr = frame.jTable1.getValueAt(row, 3).toString();
                String endTimeStr = frame.jTable1.getValueAt(row, 4).toString();

                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

                if (!now.isBefore(startTime) && !now.isAfter(endTime)) {

                    scheduleID = scheduleId;
                    frame.subject.setText(subject);
                    frame.startTime.setText(startTimeStr);
                    frame.endTime.setText(endTimeStr);

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
            String userId = user.getUser_id();
            handleMatchedStudent(userId);
        } else {
            System.out.println("No user identified.");
        }

        attendanceThread.shutdown();
    }

    private int getStudentRowIndex(String studentId) {
        for (int row = 0; row < frame.jTable2.getRowCount(); row++) {
            String idInTable = frame.jTable2.getValueAt(row, 0).toString();
            if (idInTable.equals(studentId)) {
                return row;
            }
        }
        return -1;
    }

    private void startScheduleChecker() {
        java.util.Timer timer = new java.util.Timer(true);
        timer.scheduleAtFixedRate(new java.util.TimerTask() {
            @Override
            public void run() {
//                LocalTime now = LocalTime.now();
                LocalTime now = LocalTime.parse(testTime);
                DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("h:mm a");

                for (int row = 0; row < frame.jTable1.getRowCount(); row++) {
                    String startTimeStr = frame.jTable1.getValueAt(row, 3).toString();
                    String endTimeStr = frame.jTable1.getValueAt(row, 4).toString();
                    String scheduleId = frame.jTable1.getValueAt(row, 0).toString();

                    try {
                        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
                        LocalTime graceEndTime = endTime.plusMinutes(15);

                        if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                            checkAndLoadStudents();
                        }
                        if (now.isAfter(graceEndTime)) {
                            dao.markAbsent(scheduleId);
                            System.out.println("Grace period ended. Marking absentees.");
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
        frame.jTable2.setModel(model);
        checkAndLoadStudents();
    }

    private void startScanLoop() {
//        if (scanTimer != null && scanTimer.isRunning()) {
//            scanTimer.stop(); // avoid duplicate timers
//        }
//
//        scanTimer = new Timer(5000, e -> checkAndVerifyStudents()); // every 5 seconds
//        scanTimer.setInitialDelay(0); // start immediately
//        scanTimer.start();
//
//        System.out.println("Started fingerprint scan loop.");
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
            StudentModel matchedStudent = studentMap.get(userId);

            String studentId = matchedStudent.getStud_id();

            if (matchedStudent == null || matchedStudent.getStud_id() == null) {
                JOptionPane.showMessageDialog(null,
                        "Student with ID " + userId + " is NOT in this class schedule.");
                return;
            }

            int rowIndex = getStudentRowIndex(studentId);
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " is enrolled but not found in the table.\n"
                        + "Please refresh the class roster or check for mismatches.");
                return;
            }

            String timeInRow = frame.jTable2.getValueAt(rowIndex, 3).toString();
            String timeOutRow = frame.jTable2.getValueAt(rowIndex, 4).toString();
            String scheduledStartTime = frame.startTime.getText();
            String scheduledEndTime = frame.endTime.getText();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(scheduledStartTime, timeFormatter);
            LocalTime endTime = LocalTime.parse(scheduledEndTime, timeFormatter);

            long classDuration = Duration.between(startTime, endTime).toMinutes();
            long currentDuration = Duration.between(startTime, now).toMinutes();

            if (!timeInRow.isEmpty()) {
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " has already timed in.");
            } else {
                String timeIn = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
                frame.jTable2.setValueAt(timeIn, rowIndex, 3);
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " is in current class.\nAttendance marked.");
                showStudentInfo(studentId);
//                String status = now.isAfter(startTime.plusMinutes(15)) ? "Late" : "Present";
//                dao.saveAttendance(studentId, scheduleID);
            }
            if (!timeOutRow.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Student already timed out.");
                return;
            }
            if (!timeInRow.isEmpty()) {
                String timeOut = now.format(DateTimeFormatter.ofPattern("hh: mm a"));
                frame.jTable2.setValueAt(timeOut, rowIndex, 4);
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
        for (int row = 0; row < frame.jTable2.getRowCount(); row++) {
            String scheduleId = scheduleID;
            String studentId = frame.jTable2.getValueAt(row, 0).toString();
            String timeInStr = frame.jTable2.getValueAt(row, 3).toString();
            String timeOutStr = frame.jTable2.getValueAt(row, 4).toString();
            String startTimeStr = frame.startTime.getText().trim();
            String endTimeStr = frame.endTime.getText().trim();

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");

            try {
                LocalTime startTime = LocalTime.parse(startTimeStr, formatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, formatter);
                LocalTime graceEndTime = endTime.plusMinutes(15);
                LocalTime late = startTime.plusMinutes(15);

                LocalTime timeIn = timeInStr.isEmpty() ? null : LocalTime.parse(timeInStr, formatter);
                LocalTime timeOut = timeOutStr.isEmpty() ? null : LocalTime.parse(timeOutStr, formatter);

                String status;

                if (timeOut == null) {
                    status = "INCOMPLETE";
                } else if (timeIn.isBefore(late) && !timeOut.isAfter(graceEndTime)) {
                    status = "COMPLETE";
                } else if (timeIn.isAfter(late)) {
                    status = "LATE";
                } else if (timeOut.isBefore(endTime)) {
                    status = "Early Time Out";
                } else {
                    status = "INCOMPLETE";
                }

                AttModel att = new AttModel();
                att.setCs_id(scheduleId);
                att.setStud_id(studentId);
                att.setStatus(status);
                att.setTimeIn(timeIn);
                att.setTimeOut(timeOut);

                dao.saveAttendance(att);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showStudentInfo(String studentID) {
        CardLayout cl = (CardLayout) frame.jPanel1.getLayout();
        frame.jPanel1.add(panel, "showInfo");

        List<AttModel> history = dao.getAttendanceHistory(studentID);

        if (history.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "No attendance history found for this student.");
            return;
        }

        StudentModel student = history.get(0).getStudent();
        if (student == null) {
            JOptionPane.showMessageDialog(frame, "Student information is missing.");
            return;
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

        byte[] fingerprintBytes = student.getFingerprintImage();
        if (fingerprintBytes != null && fingerprintBytes.length > 0) {
            ImageIcon icon = new ImageIcon(fingerprintBytes);
            Image scaled = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            panel.image.setIcon(new ImageIcon(scaled));
        } else {
            panel.image.setText("No image available");
        }

        DefaultTableModel model = new DefaultTableModel(new String[]{
            "Subject", "Date", "Status", "Time In", "Time Out"
        }, 0);
        for (AttModel r : history) {
            model.addRow(new Object[]{
                r.getSubject(),
                r.getAttDateTime(),
                r.getStatus(),
                r.getTimeIn(),
                r.getTimeOut()
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
            cl.show(frame.jPanel1, "showInfo");
            ((Timer) e.getSource()).stop();
        }).start();
    }

}
