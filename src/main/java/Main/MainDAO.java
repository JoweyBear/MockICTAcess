package Main;

import Attendance.AttModel;
import Student.StudentModel;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public interface MainDAO {

    DefaultTableModel fetchSchedulesForToday();

//    DefaultTableModel fetchStudentsBySchedule(String scheduleId);

    void markAbsent(String scheduleId);

    List<StudentModel>  fetchStudentsBySchedule(String scheduleId);
    
    void saveAttendance(AttModel att);
}
