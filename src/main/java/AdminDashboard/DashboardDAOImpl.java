package AdminDashboard;

import Attendance.AttModel;
import Connection.Ticket;
import Utilities.AttendanceFilterType;
import Utilities.Encryption;
import Utilities.GlobalVar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
    PreparedStatement ps = null;
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
                + "JOIN user s ON a.user_id = s.user_id "
                + "JOIN student_info si ON a.user_id = si.user_id "
                + "WHERE s.college = ? ";

        String sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count FROM attendance a "
                + "JOIN user s ON s.user_id = a.user_id "
                + "WHERE s.college = ? "
                + "GROUP BY a.user_id, a.status";

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

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE u.college = ? "
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
                + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count, "
                + "COUNT(CASE WHEN a.status = 'Early Time Out' THEN 1 END) AS left_early "
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

    @Override
    public DefaultTableModel getAllIrregularAttendance() {
        String sql = "WITH filtered_attendance AS "
                + "(SELECT * FROM attendance "
                + "WHERE (time_in IS NOT NULL AND time_out IS NULL) "
                + "OR (status = 'Late') "
                + "OR (status = 'Early Time Out') "
                + "OR (status = 'Absent' AND time_in IS NULL AND time_out IS NULL)), "
                + "summary_counts AS "
                + "(SELECT user_id, "
                + "COUNT(time_in) AS time_in_count, "
                + "COUNT(time_out) AS time_out_count, "
                + "SUM(CASE WHEN status = 'Late' THEN 1 ELSE 0 END) AS late_count, "
                + "SUM(CASE WHEN status = 'Early Time Out' THEN 1 ELSE 0 END) AS left_early_count, "
                + "SUM(CASE WHEN status = 'Absent' AND time_in IS NULL AND time_out IS NULL THEN 1 ELSE 0 END) AS absent_count "
                + "FROM attendance GROUP BY user_id)"
                + "SELECT f.user_id AS 'Student ID', u.fname AS 'firstName', u.mname AS 'middleName', u.lname AS 'lastName', "
                //                + "DATE(f.att_date_time) AS 'Date', f.status AS 'Status', f.time_in AS 'Time In', f.time_out AS 'Time Out', "
                + "s.time_in_count AS 'timeInCount', s.time_out_count AS 'timeOutCount', "
                + "s.late_count AS 'lateCount', "
                + "s.left_early_count AS 'leftEarlyCount', "
                + "s.absent_count As 'absentCount'"
                + "FROM filtered_attendance f "
                + "JOIN summary_counts s ON f.user_id = s.user_id "
                + "JOIN user u ON u.user_id = s.user_id "
                + "WHERE u.college = ? "
                + "ORDER BY f.user_id, DATE(f.att_date_time)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();
            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Student Name", "In Count", "Out Count", "Late", "Early Out", "Absent"));
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
//                row.add(rs.getString("Date"));
//                row.add(rs.getString("Time In"));
//                row.add(rs.getString("Time Out"));
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
        Vector<String> column = new Vector<>(Arrays.asList("Student ID", "Student Name", "In Count", "Out Count", "Late", "Early Out", "Absent"));
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
    public DefaultTableModel getAttendanceRecords(AttendanceFilterType filterType, Object... params) {
        String sql = "";
        String sqlStatusCounts = "";

        switch (filterType) {
            case BY_DATE_RANGE:
                sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                        + "u.fname AS 'FirstName', u.mname AS 'MiddleName', u.lname AS 'LastName', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                        + "FROM attendance a "
                        + "JOIN user u ON a.user_id = u.user_id "
                        + "JOIN student_info si ON a.user_id = si.user_id "
                        + "WHERE u.college = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ?";

                sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user s ON s.user_id = a.user_id "
                        + "WHERE s.college = ? "
                        + "AND DATE(att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY a.user_id, a.status";
                break;

            case BY_CLASS_SCHEDULE:
                sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                        + "s.fname AS 'FirstName', s.mname AS 'MiddleName', s.lname AS 'LastName', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                        + "FROM attendance a "
                        + "JOIN user s ON a.user_id = s.user_id "
                        + "JOIN student_info si ON a.user_id = si.user_id "
                        + "WHERE s.college = ? AND a.class_schedule_id = ?";

                sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user s ON a.user_id = s.user_id "
                        + "WHERE s.college = ? AND a.class_schedule_id = ? "
                        + "GROUP BY a.user_id, a.status";
                break;

            case BY_DATE_RANGE_CS:
                sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                        + "u.fname AS 'FirstName', u.mname AS 'MiddleName', u.lname AS 'LastName', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                        + "FROM attendance a "
                        + "JOIN user u ON a.user_id = u.user_id "
                        + "JOIN student_info si ON a.user_id = si.user_id "
                        + "WHERE u.college = ? "
                        + "AND a.class_schedule_id = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ?";

                sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user s ON s.user_id = a.user_id "
                        + "WHERE s.college = ? "
                        + "AND a.class_schedule_id = ? "
                        + "AND DATE(att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY a.user_id, a.status";
                break;

            case BY_FACULTY:
                sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                        + "s.fname AS 'FirstName', s.mname AS 'MiddleName', s.lname AS 'LastName', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track' "
                        + "FROM attendance a "
                        + "JOIN user s ON a.user_id = s.user_id "
                        + "JOIN student_info si ON a.user_id = si.user_id "
                        + "JOIN class_schedule cs ON a.class_schedule_id = cs.cs_id "
                        + "WHERE s.college = ? AND cs.faculty_user_id = ?";

                sqlStatusCounts = "SELECT a.user_id, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user s ON a.user_id = s.user_id "
                        + "JOIN class_schedule cs ON a.class_schedule_id = cs.cs_id "
                        + "WHERE s.college = ? AND cs.faculty_user_id = ? "
                        + "GROUP BY a.user_id, a.status";

                break;

        }
        System.out.println("Executing filter: " + filterType);
        Map<String, Map<String, Integer>> statusMap = new HashMap<>();

        try {
            ps = conn.prepareStatement(sqlStatusCounts);
            System.out.println("Params: ");
            for (Object param : params) {
                System.out.println(param);
            }
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

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

        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
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
    public Map<String, Integer> getAttedanceCounts(AttendanceFilterType filterType, Object... params) {
        Map<String, Integer> statusCounts = new HashMap<>();
        String sql = "";

        switch (filterType) {
            case BY_DATE_RANGE:
                sql = "SELECT a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY a.status";
                break;

            case BY_CLASS_SCHEDULE:
                sql = "SELECT a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ? "
                        + "GROUP BY a.status";
                break;

            case BY_DATE_RANGE_CS:
                sql = "SELECT a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY a.status";
                break;

            case BY_FACULTY:
                sql = "SELECT a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                        + "WHERE u.college = ? "
                        + "AND cs.faculty_user_id = ? "
                        + "GROUP BY a.status";
                break;
        }

        try {
            ps = conn.prepareStatement(sql);
            System.out.println("Params: ");
            for (Object param : params) {
                System.out.println(param);
            }
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            rs = ps.executeQuery();
            while (rs.next()) {
                statusCounts.put(rs.getString("status"), rs.getInt("count"));
                System.out.println(statusCounts);
            }

        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return statusCounts;
    }

    @Override
    public Map<String, Map<String, Integer>> getAttendanceByGender(AttendanceFilterType filterType, Object... params) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        String sql = "";
        switch (filterType) {
            case BY_DATE_RANGE:
                sql = "SELECT u.sex, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY u.sex, a.status";
                break;

            case BY_CLASS_SCHEDULE:
                sql = "SELECT u.sex, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ? "
                        + "GROUP BY u.sex, a.status";
                break;

            case BY_DATE_RANGE_CS:
                sql = "SELECT u.sex, a.status, COUNT(*) AS count "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ? "
                        + "GROUP BY u.sex, a.status";
                break;
        }

        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
            rs = ps.executeQuery();

            while (rs.next()) {
                String gender = rs.getString("sex");
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
    public AttModel getAttStatusCounts(AttendanceFilterType filterType, Object... params) {
        AttModel summary = new AttModel();
        String sql = "";

        switch (filterType) {
            case BY_DATE_RANGE:
                sql = "SELECT "
                        + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
                        + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
                        + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
                        + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
                        + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
                        + "COUNT(CASE WHEN a.status = 'Early Time Out' THEN 1 END) AS left_early  "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "WHERE u.college = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ?";

                break;

            case BY_CLASS_SCHEDULE:
                sql = "SELECT "
                        + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
                        + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
                        + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
                        + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
                        + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
                        + "COUNT(CASE WHEN a.status = 'Early Time Out' THEN 1 END) AS left_early "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN student_info si ON si.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ?";
                break;

            case BY_DATE_RANGE_CS:
                sql = "SELECT "
                        + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
                        + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
                        + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
                        + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
                        + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
                        + "COUNT(CASE WHEN a.status = 'Early Time Out' THEN 1 END) AS left_early "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN student_info si ON si.user_id = a.user_id "
                        + "WHERE u.college = ? AND a.class_schedule_id = ? "
                        + "AND DATE(a.att_date_time) BETWEEN ? AND ?";
                break;

            case BY_FACULTY:
                sql = "SELECT "
                        + "COUNT(CASE WHEN a.time_in IS NOT NULL THEN 1 END) AS time_in_count, "
                        + "COUNT(CASE WHEN a.time_out IS NOT NULL THEN 1 END) AS time_out_count, "
                        + "COUNT(CASE WHEN a.status = 'Absent' THEN 1 END) AS absent_count, "
                        + "COUNT(CASE WHEN a.status = 'Late' THEN 1 END) AS late_count, "
                        + "COUNT(CASE WHEN a.status = 'Incomplete' THEN 1 END) AS incomplete_count "
                        + "COUNT (CASE WHEN a.status = 'Early Time Out' THEN 1 END) AS left_early "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                        + "WHERE u.college = ? AND cs.faculty_user_id = ?";
                break;
        }
        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }
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

    @Override
    public Map<String, Map<String, Integer>> getAllAttendancePerSubjectByFaculty(String college, String faculty_id) {
        Map<String, Map<String, Integer>> subjectCounts = new HashMap<>();
        String sql = "SELECT cs.subject, a.status, COUNT(*) AS count "
                + "FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                + "WHERE u.college = ? "
                + "AND cs.faculty_user_id = ? "
                + "GROUP BY cs.subject, a.status";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            ps.setString(2, faculty_id);
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
    public DefaultTableModel saveAttendance(String college, String cs_id, Date date) {
        String sql = "SELECT a.user_id AS 'Student ID', a.status AS 'Status', "
                + "s.fname AS 'FirstName', s.mname AS 'MiddleName', s.lname AS 'LastName', "
                + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track', "
                + "a.time_in AS 'Time In', a.time_out AS 'Time Out', a.status AS 'Status' "
                + "FROM attendance a "
                + "JOIN user s ON a.user_id = s.user_id "
                + "JOIN student_info si ON a.user_id = a.user_id "
                + "WHERE s.college = ? AND a.class_schedule_id = ? AND DATE(a.att_date_time) = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            ps.setString(2, cs_id);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            ps.setDate(3, sqlDate);

            rs = ps.executeQuery();
            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "First Name", "Middle Name", "Last Name",
                    "Year", "Section", "Track", "Time In", "Time Out", "Status"));

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String studentId = rs.getString("Student ID");
                String fname = de.decrypt(rs.getString("FirstName"));
                String mname = de.decrypt(rs.getString("MiddleName"));
                String lname = de.decrypt(rs.getString("LastName"));

                row.add(studentId);
                row.add(fname);
                row.add(mname);
                row.add(lname);
                row.add(rs.getString("Section"));
                row.add(rs.getString("Year"));
                row.add(rs.getString("Track"));
                row.add(rs.getString("Time In"));
                row.add(rs.getString("Time Out"));
                row.add(rs.getString("Status"));

                data.add(row);
            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "First Name", "Middle Name", "Last Name",
                "Year", "Section", "Track", "Time In", "Time Out", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);

    }

    @Override
    public DefaultTableModel getAttendanceByPeriod(AttendanceFilterType filterType, Object... params) {
        String sql = "";
        switch (filterType) {
            case BY_MORNING:
                sql = "SELECT a.user AS 'Student ID', a.status AS 'Status', a.time_in AS 'Time In', a.time_out AS 'Time Out', "
                        + "u.fname AS 'First Name', u.mname AS 'Middle Name', u.lname AS 'Last Name', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track', "
                        + "cs.subject AS 'Subject' "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN student_info si ON u.user_id = a.user_id "
                        + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                        + "WHERE u.college = ? AND DATE(a.att_date_time) = CURDATE() "
                        + "AND TIME(a.att_date_time) BETWEEN '06:00:00' AND '12:00:00'";
                break;
            case BY_AFTERNOON:
                sql = "SELECT a.user AS 'Student ID', a.status AS 'Status', a.time_in AS 'Time In', a.time_out AS 'Time Out', "
                        + "u.fname AS 'First Name', u.mname AS 'Middle Name', u.lname AS 'Last Name', "
                        + "si.section AS 'Section', si.year AS 'Year', si.track AS 'Track', "
                        + "cs.subject AS 'Subject' "
                        + "FROM attendance a "
                        + "JOIN user u ON u.user_id = a.user_id "
                        + "JOIN student_info si ON u.user_id = a.user_id "
                        + "JOIN class_schedule cs ON cs.cs_id = a.class_schedule_id "
                        + "WHERE s.college = ? AND DATE(a.att_date_time) = CURDATE() "
                        + "AND TIME(a.att_date_time) BETWEEN '13:00:00' AND '18:00:00'";
                break;

        }

        try {
            ps = conn.prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            rs = ps.executeQuery();
            Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "First Name", "Middle Name", "Last Name",
                    "Year", "Section", "Track", "Subject", "Time In", "Time Out", "Status"));

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String studentId = rs.getString("Student ID");
                String fname = de.decrypt(rs.getString("FirstName"));
                String mname = de.decrypt(rs.getString("MiddleName"));
                String lname = de.decrypt(rs.getString("LastName"));

                row.add(studentId);
                row.add(fname);
                row.add(mname);
                row.add(lname);
                row.add(rs.getString("Section"));
                row.add(rs.getString("Year"));
                row.add(rs.getString("Track"));
                row.add(rs.getString("Subject"));
                row.add(rs.getString("Time In"));
                row.add(rs.getString("Time Out"));
                row.add(rs.getString("Status"));

                data.add(row);
            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "First Name", "Middle Name", "Last Name",
                "Year", "Section", "Track", "Subject", "Time In", "Time Out", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public AttModel getAttendanceCountsByPeriod() {
        AttModel summary = new AttModel();
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE DATE(a.att_date_time) = CURDATE() "
                + "AND TIME(a.att_date_time) BETWEEN '06:00:00' AND '12:00:00' "
                + "AND u.college = ?) AS morningCounts, "
                + "(SELECT COUNT(*) FROM attendance a "
                + "JOIN user u ON u.user_id = a.user_id "
                + "WHERE DATE(a.att_date_time) = CURDATE() "
                + "AND TIME(a.att_date_time) BETWEEN '13:00:00' AND '18:00:00' "
                + "AND u.college = ?) AS afternoonCounts";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();

            while (rs.next()) {
                summary.setMorningCounts(rs.getInt("morningCounts"));
                summary.setAfternoonCounts(rs.getInt("afternoonCounts"));
            }
        } catch (SQLException ex) {
            Logger.getLogger(DashboardDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return summary;
    }

}
