package Main;

import Connection.Ticket;
import Utilities.Encryption;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MainDAOImpl implements MainDAO {

    Connection conn;
    ResultSet rs;
    Encryption de = new Encryption();

    public MainDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchSchedulesForToday() {
        String sql = "SELECT cs_id AS 'Schedule ID', "
                + "subject AS 'Subject', "
                + "section AS 'Section', "
                + "start_time AS 'Start Time', "
                + "end_time AS 'End Time' "
                + "FROM class_schedule "
                + "WHERE day_of_week = DAYNAME(CURDATE()) "
                + "ORDER BY start_time ASC";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
                row.add(sdf.format(rs.getTime("Time Start")));
                row.add(sdf.format(rs.getString("End Time")));

                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Schedule ID", " Subject", "Section", "Start Time", "End Time"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel fetchStudentsBySchedule(String scheduleId) {
        String sql = "SELECT u.user_id AS 'Student ID', "
                + "u.fname AS 'First Name', "
                + "u.lname AS 'Last Name' "
                + "FROM user u "
                + "JOIN student_schedule ss ON u.user_id = ss.user_id "
                + "WHERE ss.cs_id = ?";

        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, scheduleId);
            try (ResultSet rs = ps.executeQuery()) {

                ResultSetMetaData md = rs.getMetaData();
                int columnCount = md.getColumnCount();

                Vector<String> columnNames = new Vector<>(Arrays.asList("Student ID", "Firt Name", "Last Name", "Status", "Time"));

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();

                    String firstName = de.decrypt(rs.getString("Student ID"));
                    String lastName = de.decrypt(rs.getString("Last Name"));

                    row.add(rs.getString("Student ID"));
                    row.add(firstName);
                    row.add(lastName);
                    row.add("Pending");
                    row.add("Pending");
                    data.add(row);
                }

                return new DefaultTableModel(data, columnNames);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "First Name", "Last Name", "Status", "Time"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

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
        String sql = "INSERT INTO attendance (student_user_id, class_schedule_id, att_date_time, status)\n"
                + "SELECT ?, ?, NOW(), ? "
                + "FROM DUAL "
                + "WHERE NOT EXISTS (SELECT 1 FROM attendance "
                + "WHERE student_user_id = ? AND class_schedule_id = ?"
                + " AND DATE(att_date_time) = CURDATE());";
        
    }

}
