package AdminDashboard;

import Attendance.AttModel;
import Connection.Ticket;
import Utilities.Encryption;
import Utilities.GlobalVar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class DashboardDAOImpl implements DashboardDAO {

    ResultSet rs;
    Connection conn;
    private String college = GlobalVar.loggedInAdmin.getCollge();
    Encryption de = new Encryption();

    public DashboardDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel getAllAttendanceRecords() {

        String sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                + "s.fname AS 'FirstName', s.mname AS 'MiddleName', s.lname AS 'LastName', "
                + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                + "FROM attendance a "
                + "JOIN user u ON a.user_id = u.user_id "
                + "JOIN student_info si ON a.user_id = a.user_id "
                + "WHERE s.college = ? ";

        String sqlStatusCounts = "SELECT user_id, status, COUNT(*) AS count FROM attendance "
                + "WHERE college = ? "
                + "GROUP BY user_id, status";

        Map<String, Map<String, Integer>> statusMap = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sqlStatusCounts)) {
            ps.setString(1, college);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String status = rs.getString("status");
                int count = rs.getInt("count");

                statusMap.computeIfAbsent(userId, k -> new HashMap<>()).put(status, count);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();

            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String studentId = rs.getString("Student ID");
                String fname = de.decrypt(rs.getString("FirstName"));
                String mname = de.decrypt(rs.getString("MiddleName")).substring(0, 1);
                String lname = de.decrypt(rs.getString("LastName"));

                String name = fname + " " + mname + ". " + lname;

                row.add(studentId);
                row.add(name);
                row.add(rs.getString("Section"));
                row.add(rs.getString("Year"));
                row.add(rs.getString("Track"));

                Map<String, Integer> counts = statusMap.getOrDefault(studentId, new HashMap<>());
                int present = counts.getOrDefault("Present", 0);
                int absent = counts.getOrDefault("Absent", 0);
                int late = counts.getOrDefault("Late", 0);
                int incomplete = counts.getOrDefault("Incomplete", 0);
                int total = present + absent + late + incomplete;

                String remarks;
                if (total == 0) {
                    remarks = "No Records";
                } else if (present > absent && present > late && present > incomplete) {
                    remarks = "Mostly Present";
                } else if (absent > present && absent > late && absent > incomplete) {
                    remarks = "Mostly Absent";
                } else if (incomplete > present && incomplete > absent && incomplete > late) {
                    remarks = "Mostly Incomplete";
                } else if (late >= 3) {
                    remarks = "Tardy";
                } else {
                    remarks = "Unclassified";
                }

                row.add(remarks);

                data.add(row);
            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", "Remarks"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public Map<String, Integer> getAttendanceStatusCounts() {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "SELECT a.status, COUNT(*) AS count "
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ? "
                + "GROUP BY a.status";

        try (PreparedStatement ps = conn.prepareStatement(sql);) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            while (rs.next()) {
                statusCounts.put(rs.getString("status"), rs.getInt("count"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return statusCounts;
    }

    @Override
    public Map<String, Map<String, Integer>> getAttendanceStatusByGender() {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        String sql = "SELECT u.gender, a.status, COUNT(*) AS count "
                + "FROM attendance a"
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ? "
                + "GROUP BY u.gender, a.status";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, college);
            rs = ps.executeQuery();

            while (rs.next()) {
                String gender = rs.getString("gender");
                String status = rs.getString("status");
                int count = rs.getInt("count");

                result.computeIfAbsent(gender, k -> new HashMap<>()).put(status, count);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public AttModel getAttendanceStatus() {
        AttModel summary = new AttModel();
        String sql = "SELECT "
                + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
                + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
                + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
                + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
                + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, college);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    summary.setTimeInCount(rs.getInt("time_in_count"));
                    summary.setTimeOutCount(rs.getInt("time_out_count"));
                    summary.setAbsentCount(rs.getInt("absent_count"));
                    summary.setLateCount(rs.getInt("late_count"));
                    summary.setIncompleteCount(rs.getInt("incomplete_count"));
                }
            }
        } catch (SQLException e) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, e);
        }

        return summary;
    }
//SELECT a.*, s.fname, s.mname, s.lname
//FROM attendance a
//JOIN student s ON a.user_id = s.user_id
//WHERE a.faculty_id = ?
//        
//SELECT * FROM attendance
//WHERE date BETWEEN ? AND ?
//  AND subject_id = ?
//  AND section = ?
//  AND year = ?
//          
//          SELECT * FROM attendance
//WHERE subject_id = ?
//  AND section = ?
//  AND year = ?
//          
//SELECT * FROM attendance
//WHERE date BETWEEN ? AND ?
    
//    SELECT a.*, f.fname, f.mname, f.lname
//FROM attendance a
//JOIN user f ON a.user_id = f.user_id
//WHERE a.user_id = ?

}
