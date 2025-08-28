
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
    

}
