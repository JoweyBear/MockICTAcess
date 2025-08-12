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
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MainSerImpl implements MainService {

    MainFrame frame;
    MainDAO dao = new MainDAOImpl();

    public MainSerImpl(MainFrame frame) {
        this.frame = frame;

        checkAndLoadStudents(frame.jTable2, frame.jTable1, 3, 4, 0);
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
    public void checkAndLoadStudents(JTable tableA, JTable tableB, int startCol, int endCol, int scheduleIdCol) {
        LocalTime now = LocalTime.now();
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        for (int row = 0; row < tableA.getRowCount(); row++) {
            try {
                String startTimeStr = tableA.getValueAt(row, startCol).toString();
                String endTimeStr = tableA.getValueAt(row, endCol).toString();

                LocalTime startTime = LocalTime.parse(startTimeStr, timeFormatter);
                LocalTime endTime = LocalTime.parse(endTimeStr, timeFormatter);

                if (!now.isBefore(startTime) && !now.isAfter(endTime)) {
                    String scheduleId = tableA.getValueAt(row, scheduleIdCol).toString();

                    DefaultTableModel studentsModel = dao.fetchStudentsBySchedule(scheduleId);
                    tableB.setModel(studentsModel);

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
        try {
            Selection.resetReader();
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();

            if (readers.size() == 0 || readers.get(0) == null) {
                JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
                return;
            }
            Selection.reader = readers.get(0);

            IdentificationThread idThread = new IdentificationThread(null, null);
            idThread.start();
            idThread.join();

            FingerprintModel matchedStudent = idThread.getIdentifiedUser();

            // If match found, check schedule
            if (matchedStudent != null) {
                String studentId = matchedStudent.getUser_id();

                if (ServiceLayer.isStudentInCurrentClass(studentId)) {
                    JOptionPane.showMessageDialog(null,
                            "Student " + matchedStudent.getFname() + " is in current class.\nAttendance marked.");
                    // ServiceLayer.markAttendance(studentId);
                } else {
                    JOptionPane.showMessageDialog(null,
                            "Student " + matchedStudent.getFname() + " is NOT in this class schedule.");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No matching fingerprint found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error: " + e.getMessage());
        }
    }

    public boolean isStudentInCurrentSchedule(String studentId) {
        for (int row = 0; row < frame.jTable1.getRowCount(); row++) {
            String idInTable = frame.jTable1.getValueAt(row, 0).toString();
            if (idInTable.equals(studentId)) {
                return true;
            }
        }
        return false;
    }

}
