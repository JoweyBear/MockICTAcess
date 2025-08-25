package Main;

import Attendance.AttModel;
import Connection.Ticket;
import Student.StudentModel;
import Utilities.Encryption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class MainDAOImpl implements MainDAO {

    Connection conn;
    ResultSet rs;
    Encryption de = new Encryption();

    String testDay = "Monday";

    public MainDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchSchedulesForToday() {
//        String sql = "SELECT cs_id AS 'Schedule ID', "
//                + "subject AS 'Subject', "
//                + "section AS 'Section', "
//                + "time_start AS 'Start Time', "
//                + "time_end AS 'End Time' "
//                + "FROM class_schedule "
//                + "WHERE day = DAYNAME(CURDATE()) "
//                + "ORDER BY time_start ASC";

        String sql = "SELECT cs_id AS 'Schedule ID', "
                + "subject AS 'Subject', "
                + "section AS 'Section', "
                + "time_start AS 'Start Time', "
                + "time_end AS 'End Time' "
                + "FROM class_schedule "
                + "WHERE day = ? "
                + "ORDER BY time_start ASC";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, testDay);
            rs = ps.executeQuery();

            ResultSetMetaData md = rs.getMetaData();
            int columnCount = md.getColumnCount();

            Vector<String> columnNames = new Vector<>(Arrays.asList("Schedule ID", "Subject", "Section", "Start Time", "End Time"));

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getString("Schedule ID"));
                row.add(rs.getString("Subject"));
                row.add(rs.getString("Section"));

                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                row.add(sdf.format(rs.getTime("Start Time")));
                row.add(sdf.format(rs.getTime("End Time")));

                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Schedule ID", " Subject", "Section", "Start Time", "End Time"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

//    @Override
//    public DefaultTableModel fetchStudentsBySchedule(String scheduleId) {
//        String sql = "SELECT u.user_id AS 'Student ID', "
//                + "u.fname AS 'First Name', "
//                + "u.lname AS 'Last Name' "
//                + "FROM class_student ss "
//                + "JOIN user u ON ss.student_user_id = u.user_id "
//                + "WHERE ss.class_schedule_id = ?";
//
//        try (PreparedStatement ps = conn.prepareStatement(sql)) {
//            ps.setString(1, scheduleId);
//            try (ResultSet rs = ps.executeQuery()) {
//
//                ResultSetMetaData md = rs.getMetaData();
//                int columnCount = md.getColumnCount();
//
//                Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "First Name", "Last Name", "Status", "Time"));
//
//                Vector<Vector<Object>> data = new Vector<>();
//                while (rs.next()) {
//                    Vector<Object> row = new Vector<>();
//
//                    String firstName = de.decrypt(rs.getString("First Name"));
//                    String lastName = de.decrypt(rs.getString("Last Name"));
//
//                    row.add(rs.getString("Student ID"));
//                    row.add(firstName);
//                    row.add(lastName);
//                    row.add("Pending");
//                    row.add("Pending");
//                    data.add(row);
//                }
//
//                return new DefaultTableModel(data, columnNames);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "First Name", "Last Name", "Status", "Time"));
//        return new DefaultTableModel(new Vector<>(), columns);
//    }
    @Override
    public void markAbsent(String scheduleId) {
        String sql = "SELECT student_user_id FROM class_student WHERE class_schedule_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    String studentId = rs.getString("student_user_id");

                    String checkSql = "SELECT COUNT(*) FROM attendance WHERE student_user_id = ? AND class_schedule_id = ? AND DATE(att_date_time) = CURDATE()";
                    try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                        psCheck.setString(1, studentId);
                        psCheck.setString(2, scheduleId);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                                String insertSql = "INSERT INTO attendance (student_user_id, class_schedule_id, att_date_time, status, method) VALUES (?, ?, ?, 'Absent', 'Auto-marked by system')";
                                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                                    psInsert.setString(1, studentId);
                                    psInsert.setString(2, scheduleId);
                                    psInsert.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                                    psInsert.executeUpdate();
//                                    add Auto-marked by system in the enum of method.
                                }
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @Override
    public List<StudentModel> fetchStudentsBySchedule(String scheduleId) {
        List<StudentModel> students = new ArrayList<>();

        String sql = "SELECT u.user_id, u.fname, u.lname FROM class_student ss "
                + "JOIN user u ON ss.student_user_id = u.user_id "
                + "WHERE ss.class_schedule_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentModel student = new StudentModel();
                    student.setStud_id(rs.getString("user_id"));
//                    student.setFullName(de.decrypt(rs.getString("fname")) + " " + de.decrypt(rs.getString("lname")));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return students;

    }

    @Override
    public void saveAttendance(AttModel att) {
        String sql = "INSERT INTO attendance (student_user_id, class_schedule_id, att_date_time, status, time_in, time_out) "
                + "SELECT ?, ?, NOW(), ?, ?, ? "
                + "FROM DUAL "
                + "WHERE NOT EXISTS (SELECT 1 FROM attendance "
                + "WHERE student_user_id = ? AND class_schedule_id = ?"
                + " AND DATE(att_date_time) = CURDATE())";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, att.getStud_id());
            ps.setString(2, att.getCs_id());
            ps.setString(3, att.getStatus());
            ps.setTime(4, Time.valueOf(att.getTimeIn()));
            ps.setTime(5, Time.valueOf(att.getTimeOut()));
            ps.setString(6, att.getStud_id());
            ps.setString(7, att.getCs_id());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MainDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


    @Override
    public Map<String, Integer> getStatusCounts(String studentId) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT status, COUNT(*) FROM attendance "
                + "WHERE student_user_id = ? "
                + "GROUP BY status";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Map<String, Integer> getSubjectAttendanceCounts(String studentId) {
        Map<String, Integer> map = new HashMap<>();
        String sql = "SELECT cs.subject, COUNT(*) FROM attendance a JOIN class_schedule cs ON a.class_schedule_id = cs.id WHERE a.student_user_id = ? GROUP BY cs.subject";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getString(1), rs.getInt(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<AttModel> getAttendanceHistory(String studentId) {
        List<AttModel> list = new ArrayList<>();
        String sql = "SELECT s.fname, s.lname, s.image, "
                + "si.year, si.section, si.track, "
                + "i.fingerprint_image, "
                + "cs.subject, a.att_date_time, a.status, a.time_in, a.time_out "
                + "FROM attendance a "
                + "JOIN class_schedule cs ON a.class_schedule_id = cs.id "
                + "JOIN user s ON a.student_user_id = s.user_id "
                + "JOIN student_info si ON a.student_user_id = si.user_id "
                + "JOIN identification i ON a.student_user_id = i.user_id "
                + "WHERE a.student_user_id = ? "
                + "ORDER BY a.att_date_time DESC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    StudentModel student = new StudentModel();
                    String fname = de.decrypt(rs.getString("fname"));
                    String lname = de.decrypt(rs.getString("lname"));
                    
                    AttModel record = new AttModel();
                    record.setSubject(rs.getString("subject_name"));
                    record.setAttDateTime(rs.getTimestamp("att_date_time").toLocalDateTime());
                    record.setStatus(rs.getString("status"));
                    record.setTimeIn(rs.getTime("time_in") != null ? rs.getTime("time_in").toLocalTime() : null);
                    record.setTimeOut(rs.getTime("time_out") != null ? rs.getTime("time_out").toLocalTime() : null);
                    list.add(record);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }




}
