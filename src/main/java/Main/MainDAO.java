package Main;

import javax.swing.table.DefaultTableModel;

public interface MainDAO {
     DefaultTableModel fetchSchedulesForToday();
     DefaultTableModel fetchStudentsBySchedule(String scheduleId);
}
