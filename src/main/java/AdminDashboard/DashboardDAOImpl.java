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
            rs = ps.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String status = rs.getString("status");
                int count = rs.getInt("count");

                statusMap.computeIfAbsent(userId, k -> new HashMap<>()).merge(status, count, Integer::sum);
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
                int present = counts.getOrDefault("Complete", 0);
                int absent = counts.getOrDefault("Absent", 0);
                int late = counts.getOrDefault("Late", 0);
                int incomplete = counts.getOrDefault("Incomplete", 0);
                int leftEarly = counts.getOrDefault("Early Time Out", 0);
                int total = present + absent + late + incomplete + leftEarly;

                String remarks;
                if (total == 0) {
                    remarks = "No Records";
                } else {
                    double presentRate = (double) present / total;
                    double absentRate = (double) absent / total;
                    double lateRate = (double) late / total;
                    double incompleteRate = (double) incomplete / total;
                    double earlyRate = (double) leftEarly / total;

                    if (lateRate >= 0.3) {
                        remarks = "Tardy (" + String.format("%.0f%%", lateRate * 100) + ")";
                    } else if (presentRate >= 0.5) {
                        remarks = "Mostly Present (" + String.format("%.0f%%", presentRate * 100) + ")";
                    } else if (absentRate >= 0.5) {
                        remarks = "Mostly Absent (" + String.format("%.0f%%", absentRate * 100) + ")";
                    } else if (incompleteRate >= 0.5) {
                        remarks = "Mostly Incomplete (" + String.format("%.0f%%", incompleteRate * 100) + ")";
                    } else if (earlyRate >= 0.5) {
                        remarks = "Mostly Left Early (" + String.format("%.0f%%", earlyRate * 100) + ")";
                    } else {
                        remarks = "Unclassified";
                    }
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
        String sql = "SELECT u.sex, a.status, COUNT(*) AS count "
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

                result.computeIfAbsent(gender, k -> new HashMap<>()).merge(status, count, Integer::sum);

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
                + "COUNT (CASE WHEN a.status = 'Early Time Out' THEN 1 END AS left_early) "
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            if (rs.next()) {
                summary.setTimeInCount(rs.getInt("time_in_count"));
                summary.setTimeOutCount(rs.getInt("time_out_count"));
                summary.setAbsentCount(rs.getInt("absent_count"));
                summary.setLateCount(rs.getInt("late_count"));
                summary.setIncompleteCount(rs.getInt("incomplete_count"));
                summary.setLeftEarly(rs.getInt("left_early"));
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
    @Override
    public DefaultTableModel getAllIrregularAttendance() {
        String sql = "WITH filtered_attendance AS "
                + "(SELECT * FROM attendance "
                + "WHERE (time_in IS NOT NULL AND time_out IS NULL) "
                + "OR (status = 'Late') "
                + "OR (status = 'Early Time Out') "
                + "OR (status = 'Absent' AND time_in IS NULL AND time_out IS NULL)), "
                + "summary_counts AS "
                + "(SELECT student_id, "
                + "COUNT(time_in) AS time_in_count, "
                + "COUNT(time_out) AS time_out_count, "
                + "SUM(CASE WHEN status = 'Late' THEN 1 ELSE 0 END) AS late_count, "
                + "SUM(CASE WHEN status = 'Early Time Out' THEN 1 ELSE 0 END) AS left_early_count, "
                + "SUM(CASE WHEN status = 'Absent' AND time_in IS NULL AND time_out IS NULL THEN 1 ELSE 0 END) AS absent_count "
                + "FROM attendance GROUP BY student_id)"
                + "SELECT f.student_id AS 'Student ID', u.fname AS 'firstName', u.mname AS 'middleName', u.lname AS 'lastName', "
                + "DATE(f.att_date_time) AS 'Date', f.status AS 'Status', f.time_in AS 'Time In', f.time_out AS 'Time Out', "
                + "s.time_in_count AS 'timeInCount', s.time_out_count AS 'timeOutCount', "
                + "s.late_count AS 'lateCount', "
                + "s.left_early_count AS 'leftEarlyCount', "
                + "s.absent_count As 'absentCount'"
                + "FROM filtered_attendance f "
                + "JOIN summary_counts s ON f.student_id = s.student_id "
                + "JOIN user u ON u.user_id = s.student_id "
                + "WHERE u.college = ? "
                + "ORDER BY f.student_id, DATE(f.att_date_time)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Student Name", "Date", "Time in", "Time Out", "In Count", "Out Count", "Late", "Early Out", "Absent"));
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String studentID = rs.getString("Student ID");
                String fname = de.decrypt(rs.getString("firstName"));
                String mname = de.decrypt(rs.getString("middleName")).substring(0, 1);
                String lname = de.decrypt(rs.getString("lastName"));

                String name = fname + " " + mname + ". " + lname;

                row.add(studentID);
                row.add(name);
                row.add(rs.getString("Date"));
                row.add(rs.getString("Time In"));
                row.add(rs.getString("Time Out"));
                row.add(rs.getString("timeInCount"));
                row.add(rs.getString("timeOutCount"));
                row.add(rs.getString("lateCount"));
                row.add(rs.getString("leftEarlyCount"));
                row.add(rs.getString("absentCount"));

                data.add(row);

            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> column = new Vector<>(Arrays.asList("Student ID", "Student Name", "Date", "Time in", "Time Out", "In Count", "Out Count", "Late", "Early Out", "Absent"));
        return new DefaultTableModel(new Vector<>(), column);

    }

    @Override
    public Map<String, Map<String, Integer>> getAllIrregularAttendanceByGender() {
        Map<String, Map<String, Integer>> result = new HashMap();
        String sql = "SELECT u.sex, a.status, COUNT(*) AS count "
                + "FROM attendance a JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ? "
                + "AND ((a.time_in IS NOT NULL AND a.time_out IS NULL) "
                + "OR a.status IN ('Late', 'Absent', 'Early Time Out')) "
                + "GROUP BY u.sex, a.status";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            while (rs.next()) {
                String gender = rs.getString("sex");
                String status = rs.getString("status");
                int count = rs.getInt("count");

                result.computeIfAbsent(gender, k -> new HashMap<>()).merge(status, count, Integer::sum);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return result;
    }

    @Override
    public Map<String, Map<String, Integer>> getAllIrregularAttendancePerSubject() {
        Map<String, Map<String, Integer>> subjectCounts = new HashMap<>();
        String sql = "SELECT cs.subject, a.status, COUNT(*) AS count "
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                + "WHERE u.college = ? "
                + "AND ((a.time_in IS NOT NULL AND a.time_out IS NULL "
                + "OR a.status IN ('Late', 'Absent', 'Early Time Out'))) "
                + "GROUP BY cs.subject, a.status";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            while (rs.next()) {
                String subject = rs.getString("subject");
                String status = rs.getString("status");
                int count = rs.getInt("count");
                subjectCounts.computeIfAbsent(subject, k -> new HashMap<>()).merge(status, count, Integer::sum);

            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return subjectCounts;
    }

    @Override
    public DefaultTableModel getAllAttendaceRecordsBetween(String date1, String date2) {

        String sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                + "u.fname AS 'FirstName', u.mname AS 'MiddleName', u.lname AS 'LastName', "
                + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                + "FROM attendance a "
                + "JOIN user u ON a.user_id = u.user_id "
                + "JOIN student_info si ON a.user_id = si.user_id "
                + "WHERE u.college = ? "
                + "AND DATE(a.att_date_time) BETWEEN ? AND ?";

        String sqlStatusCounts = "SELECT user_id, status, COUNT(*) AS count "
                + "FROM attendance "
                + "WHERE college = ? "
                + "AND DATE(att_date_time) BETWEEN ? AND ? "
                + "GROUP BY user_id, status";

        Map<String, Map<String, Integer>> statusMap = new HashMap<>();

        try (PreparedStatement ps = conn.prepareStatement(sqlStatusCounts)) {
            ps.setString(1, college);
//            convertLocaltimeToString
            ps.setString(2, date1);
            ps.setString(3, date2);
            rs = ps.executeQuery();

            while (rs.next()) {
                String userId = rs.getString("user_id");
                String status = rs.getString("status");
                int count = rs.getInt("count");

                statusMap.computeIfAbsent(userId, k -> new HashMap<>()).merge(status, count, Integer::sum);
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            ps.setString(2, date1);
            ps.setString(3, date2);
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
                int present = counts.getOrDefault("Complete", 0);
                int absent = counts.getOrDefault("Absent", 0);
                int late = counts.getOrDefault("Late", 0);
                int incomplete = counts.getOrDefault("Incomplete", 0);
                int leftEarly = counts.getOrDefault("Early Time Out", 0);
                int total = present + absent + late + incomplete + leftEarly;

                String remarks;
                if (total == 0) {
                    remarks = "No Records";
                } else {
                    double presentRate = (double) present / total;
                    double absentRate = (double) absent / total;
                    double lateRate = (double) late / total;
                    double incompleteRate = (double) incomplete / total;
                    double earlyRate = (double) leftEarly / total;

                    if (lateRate >= 0.3) {
                        remarks = "Tardy (" + String.format("%.0f%%", lateRate * 100) + ")";
                    } else if (presentRate >= 0.5) {
                        remarks = "Mostly Present (" + String.format("%.0f%%", presentRate * 100) + ")";
                    } else if (absentRate >= 0.5) {
                        remarks = "Mostly Absent (" + String.format("%.0f%%", absentRate * 100) + ")";
                    } else if (incompleteRate >= 0.5) {
                        remarks = "Mostly Incomplete (" + String.format("%.0f%%", incompleteRate * 100) + ")";
                    } else if (earlyRate >= 0.5) {
                        remarks = "Mostly Left Early (" + String.format("%.0f%%", earlyRate * 100) + ")";
                    } else {
                        remarks = "Unclassified";
                    }
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
    public Map<String, Integer> getAttendanceCountsBetween(String date1, String date2) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
