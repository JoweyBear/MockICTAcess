package AdminDashboard;

import Attendance.AttModel;
import Utilities.AttendanceFilterType;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.swing.table.DefaultTableModel;

public interface DashboardDAO {

    DefaultTableModel getAllAttendanceRecords();

    Map<String, Integer> getAttendanceStatusCounts();

    Map<String, Map<String, Integer>> getAttendanceStatusByGender();

    AttModel getAttendanceStatus();

    DefaultTableModel getAllIrregularAttendance();

    Map<String, Map<String, Integer>> getAllIrregularAttendanceByGender();

    Map<String, Map<String, Integer>> getAllIrregularAttendancePerSubject();

//    DefaultTableModel getAllAttendaceRecordsBetween(Date date1, Date date2);
//    Map<String, Integer> getAttendanceCountsBetween(Date date1, Date date2);
//    Map<String, Map<String, Integer>> getAttendanceByGenderBetween(Date date1, Date date2);
//    AttModel getAttendanceStatusBetween(Date date1, Date date2);
//    
//    DefaultTableModel getAllAttendanceCS(String cs_id);
//    Map<String, Integer> getAttendanceCountsCS(String cs_id);
//    Map<String, Map<String, Integer>> getAttendanceByGenderCS(String cs_id);
//    AttModel getAttedanceStatusCS(String cs_id);
    DefaultTableModel getAttendanceRecords(AttendanceFilterType filterType, Object... params);

    Map<String, Integer> getAttedanceCounts(AttendanceFilterType filterType, Object... params);

    Map<String, Map<String, Integer>> getAttendanceByGender(AttendanceFilterType filterType, Object... params);

    AttModel getAttStatusCounts(AttendanceFilterType filterType, Object... params);

    Map<String, Map<String, Integer>> getAllAttendancePerSubjectByFaculty(String college, String faculty_id);
    DefaultTableModel saveAttendance(String college, String cs_id, Date date);
    
    AttModel getAttendanceCountsByPeriod();
    DefaultTableModel getAttendanceByPeriod(AttendanceFilterType filterType, Object... params);
    

}
