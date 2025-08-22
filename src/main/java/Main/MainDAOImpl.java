package Main;

import Connection.Ticket;
import Student.StudentModel;
import Utilities.Encryption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
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

                    String checkSql = "SELECT COUNT(*) FROM attendance WHERE student_user_id = ? AND class_schedule_id = ? AND date = CURDATE()";
                    try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                        psCheck.setString(1, studentId);
                        psCheck.setString(2, scheduleId);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next() && rsCheck.getInt(1) == 0) {
                                String insertSql = "INSERT INTO attendance (student_user_id, class_schedule_id, date, status) VALUES (?, ?, CURDATE(), 'Absent')";
                                try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                                    psInsert.setString(1, studentId);
                                    psInsert.setString(2, scheduleId);
                                    psInsert.executeUpdate();
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
    public void saveAttendance(String studentId, String scheduleId) {
        String sql = "INSERT INTO attendance (student_user_id, class_schedule_id, att_date_time, status) "
                + "SELECT ?, ?, NOW(), ? "
                + "FROM DUAL "
                + "WHERE NOT EXISTS (SELECT 1 FROM attendance "
                + "WHERE student_user_id = ? AND class_schedule_id = ?"
                + " AND DATE(att_date_time) = CURDATE())";

        try {
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, studentId);
            ps.setString(2, scheduleId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(MainDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
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

    public void saveAttendanceForSchedule(String scheduleId, Map<String, LocalTime> scannedStudents) {
        String getStudentsSql = "SELECT u.user_id, u.fname, u.lname "
                + "FROM class_student cs "
                + "JOIN user u ON cs.student_user_id = u.user_id "
                + "WHERE cs.class_schedule_id = ?";

        String insertAttendanceSql = "INSERT INTO attendance (schedule_id, student_id, status, time_in) "
                + "VALUES (?, ?, ?, ?)";

        try (PreparedStatement psGet = conn.prepareStatement(getStudentsSql); PreparedStatement psInsert = conn.prepareStatement(insertAttendanceSql)) {

            psGet.setString(1, scheduleId);
            try (ResultSet rs = psGet.executeQuery()) {
                while (rs.next()) {
                    String studentId = rs.getString("user_id");
                    LocalTime timeIn = scannedStudents.get(studentId);

                    String status;
                    if (timeIn == null) {
                        status = "INCOMPLETE"; // student never scanned
                    } else {
                        // You can add more nuanced logic here if needed
                        status = "COMPLETE"; // or "LATE" based on timeIn
                    }

                    psInsert.setString(1, scheduleId);
                    psInsert.setString(2, studentId);
                    psInsert.setString(3, status);
                    psInsert.setString(4, timeIn != null ? timeIn.toString() : null);
                    psInsert.addBatch();
                }
                psInsert.executeBatch();
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
