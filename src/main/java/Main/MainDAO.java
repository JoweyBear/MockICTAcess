package Main;

import javax.swing.table.DefaultTableModel;

public interface MainDAO {
     DefaultTableModel fetchSchedulesForToday();
     DefaultTableModel fetchStudentsBySchedule(String scheduleId);
     void saveAttendance(String studentId, String scheduleId);
     void markAbsent(String scheduleId);
}
