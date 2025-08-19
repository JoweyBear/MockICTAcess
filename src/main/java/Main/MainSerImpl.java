package Main;

import Fingerprint.FingerprintModel;
import Fingerprint.IdentificationThread;
import Fingerprint.Selection;
import Main.Views.MainFrame;
import Login.*;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUGlobal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

public class MainSerImpl implements MainService {
    
    MainFrame frame;
    MainDAO dao = new MainDAOImpl();
    private String scheduleID;
    private final ExecutorService scanExecutor = Executors.newSingleThreadExecutor();
    private volatile boolean scanningActive = false;
    
    private String testTime = "13:00:00";
    
    public MainSerImpl(MainFrame frame) {
        this.frame = frame;
        
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
        
        for (int row = 0; row < frame.jTable2.getRowCount(); row++) {
            try {
                String scheduleId = frame.jTable2.getValueAt(row, 0).toString();
                String subject = frame.jTable2.getValueAt(row, 1).toString();
                String startTimeStr = frame.jTable2.getValueAt(row, 3).toString();
                String endTimeStr = frame.jTable2.getValueAt(row, 4).toString();
                
                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
                
                if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                    
                    scheduleID = scheduleId;
                    frame.subject.setText(subject);
                    frame.startTime.setText(startTimeStr);
                    frame.endTime.setText(endTimeStr);
                    
                    DefaultTableModel studentsModel = dao.fetchStudentsBySchedule(scheduleId);
                    frame.jTable1.setModel(studentsModel);
                    
//                    startScanLoop();
                    
                    System.out.println("Loaded students for schedule: " + scheduleId);
                    break;
                }
                
            } catch (Exception e) {
                System.err.println("Error parsing time for row " + row + ": " + e.getMessage());
            }
        }

    }
    
    @Override
//    public void checkAndVerifyStudents() {
//        try {
//            Selection.resetReader();
//            ReaderCollection readers = UareUGlobal.GetReaderCollection();
//            readers.GetReaders();
//
//            if (readers.size() == 0 || readers.get(0) == null) {
//                JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
//                return;
//            }
//            Selection.reader = readers.get(0);
//
//            IdentificationThread idThread = new IdentificationThread(null, null);
//            idThread.start();
//            idThread.join();
//
//            FingerprintModel matchedStudent = idThread.getIdentifiedUser();
//
//            if (matchedStudent != null) {
//                String studentId = matchedStudent.getUser_id();
//
//                int rowIndex = getStudentRowIndex(studentId);
//
//                if (rowIndex == -1) {
//                    JOptionPane.showMessageDialog(null,
//                            "Student " + matchedStudent.getFname() + " is NOT in this class schedule.");
//                } else {
//                    String currentStatus = frame.jTable1.getValueAt(rowIndex, 3).toString();
//                    String scheduledStartTime = frame.startTime.getText();
//                    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
//                    LocalTime now = LocalTime.now();
//                    LocalTime startTime = LocalTime.parse(scheduledStartTime, timeFormatter);
//                    String timeIn = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
//
//                    if ("Present".equalsIgnoreCase(currentStatus) || "Late".equalsIgnoreCase(currentStatus)) {
//                        JOptionPane.showMessageDialog(null,
//                                "Student " + matchedStudent.getFname() + " has already been marked.");
//                    } else {
//                        String status = now.isAfter(startTime.plusMinutes(15)) ? "Late" : "Present";
//
//                        frame.jTable1.setValueAt(status, rowIndex, 3);
//                        frame.jTable1.setValueAt(timeIn, rowIndex, 4);
//                        JOptionPane.showMessageDialog(null,
//                                "Student " + matchedStudent.getFname() + " is in current class.\nAttendance marked.");
//
//                        System.out.println("Class ID: " + scheduleID);
//
//                        dao.saveAttendance(studentId, scheduleID);
//
//                        // Optionally, save to DB here
//                        // ServiceLayer.markAttendance(studentId);
//                    }
//                }
//            } else {
//                JOptionPane.showMessageDialog(null, "No matching fingerprint found.");
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
//        }
//    }
    public void checkAndVerifyStudents() {
        // Run heavy fingerprint + DB logic off the EDT
        new Thread(() -> {
            try {
//                Selection.resetReader();
                ReaderCollection readers = UareUGlobal.GetReaderCollection();
                readers.GetReaders();
                
                if (readers.size() == 0 || readers.get(0) == null) {
                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(null, "No fingerprint reader found.")
                    );
                    return;
                }
                Selection.reader = readers.get(0);
                
                IdentificationThread idThread = new IdentificationThread();
                idThread.start();
                try {
                    idThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                
                FingerprintModel matchedStudent = idThread.getIdentifiedUser();
                
                if (matchedStudent != null) {
                    handleMatchedStudent(matchedStudent);
                } else {
                    SwingUtilities.invokeLater(()
                            -> JOptionPane.showMessageDialog(null, "No matching fingerprint found.")
                    );
                }
                
            } catch (Exception e) {
                e.printStackTrace();
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(null, "Error: " + e.getMessage())
                );
            }
        }).start();
    }
    
    private void checkThreadsActivity() {
        if (Selection.reader != null && Selection.readerIsConnected()) {
            System.out.println("Reader is already open and connected");
            
            if (Selection.isAnotherThreadCapturing()) {
                checkAndVerifyStudents();
            } else {
                System.out.println("Another capture uis in progress -- cancelling..");
                Selection.requestCurrentCaptureCancel();
                Selection.waitForCaptureToFinish();
                checkAndVerifyStudents();
            }
        } else {
            System.out.println("Reader is not open..Opeining..");
            Selection.resetReader();
            checkAndVerifyStudents();
        }
        
    }
    
    private int getStudentRowIndex(String studentId) {
        for (int row = 0; row < frame.jTable1.getRowCount(); row++) {
            String idInTable = frame.jTable1.getValueAt(row, 0).toString();
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
                
                for (int row = 0; row < frame.jTable2.getRowCount(); row++) {
                    String startTimeStr = frame.jTable2.getValueAt(row, 3).toString();
                    String endTimeStr = frame.jTable2.getValueAt(row, 4).toString();
                    String scheduleId = frame.jTable2.getValueAt(row, 0).toString();
                    
                    try {
                        LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                        LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);
                        
                        if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                            checkAndLoadStudents();
                        }
                        if (now.isAfter(endTime)) {
                            dao.markAbsent(scheduleId);
                            stopScanLoop();
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
                checkThreadsActivity();
//                checkAndVerifyStudents(); // runs async, already non-blocking
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
    
    private void handleMatchedStudent(FingerprintModel matchedStudent) {
        SwingUtilities.invokeLater(() -> {
            String studentId = matchedStudent.getUser_id();
            int rowIndex = getStudentRowIndex(studentId);
            
            if (rowIndex == -1) {
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " is NOT in this class schedule.");
                return;
            }
            
            String currentStatus = frame.jTable1.getValueAt(rowIndex, 3).toString();
            String scheduledStartTime = frame.startTime.getText();
            DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime now = LocalTime.now();
            LocalTime startTime = LocalTime.parse(scheduledStartTime, timeFormatter);
            String timeIn = now.format(DateTimeFormatter.ofPattern("hh:mm a"));
            
            if ("Present".equalsIgnoreCase(currentStatus) || "Late".equalsIgnoreCase(currentStatus)) {
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " has already been marked.");
            } else {
                String status = now.isAfter(startTime.plusMinutes(15)) ? "Late" : "Present";
                frame.jTable1.setValueAt(status, rowIndex, 3);
                frame.jTable1.setValueAt(timeIn, rowIndex, 4);
                
                JOptionPane.showMessageDialog(null,
                        "Student " + matchedStudent.getFname() + " is in current class.\nAttendance marked.");
                
                dao.saveAttendance(studentId, scheduleID);
            }
        });
    }
    
}
