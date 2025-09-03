package Main;

import Attendance.AttModel;
import Faculty.FacultyModel;
import Fingerprint.FingerprintModel;
import Student.StudentModel;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public interface MainDAO {

    DefaultTableModel fetchSchedulesForToday();

//    DefaultTableModel fetchStudentsBySchedule(String scheduleId);
    void markAbsent(String scheduleId);

    List<StudentModel> fetchStudentsBySchedule(String scheduleId);

    boolean saveAttendance(AttModel att);

    boolean hasTimeIn(String userId, String scheduleId);
    
    void saveTimeIn(String userId, String scheduleId, LocalTime now);
    
    void markIncompete(String userId, String scheduleId, LocalTime timeIn);

    StudentModel fetchStudentInfo(String studentId);

    FacultyModel getAssignedFacultyInfo(String csId, String facultyId);

    Map<String, Integer> getStatusCounts(String userId);

    List<AttModel> getAttendanceHistory(String userId);

    Map<String, Integer> getSubjectAttendanceCounts(String studentId);

    Map<String, Map<String, Integer>> getSectionAttendanceCounts(String facultyId);

    FingerprintModel userIdMatched(String user_id);
}
