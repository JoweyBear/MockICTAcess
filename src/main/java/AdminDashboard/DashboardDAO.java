
package AdminDashboard;

import Attendance.AttModel;
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
    
    DefaultTableModel getAllAttendaceRecordsBetween(String date1, String date2);
    Map<String, Integer> getAttendanceCountsBetween(String date1, String date2);
    Map<String, Map<String, Integer>> getAttendanceByGenderBetween(String date1, String date2);
    AttModel getAttendanceStatusBetween(String date1, String date2);
    
    

}
