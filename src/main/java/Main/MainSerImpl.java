package Main;

import Main.Views.MainFrame;
import Login.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
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

}
