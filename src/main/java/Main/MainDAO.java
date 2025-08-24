package Main;

import Attendance.AttModel;
import Student.StudentModel;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public interface MainDAO {

    DefaultTableModel fetchSchedulesForToday();

//    DefaultTableModel fetchStudentsBySchedule(String scheduleId);
    void markAbsent(String scheduleId);

    List<StudentModel> fetchStudentsBySchedule(String scheduleId);

    void saveAttendance(AttModel att);

    Map<String, Integer> getStatusCounts(String studentId);

    Map<String, Integer> getSubjectAttendanceCounts(String studentId);

    List<AttModel> getAttendanceHistory(String studentId);

}
