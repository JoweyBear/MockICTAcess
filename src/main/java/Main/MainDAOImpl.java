package Main;

import Connection.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;
import javax.swing.table.DefaultTableModel;

public class MainDAOImpl implements MainDAO {

    Connection conn;
    ResultSet rs;

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

            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(md.getColumnLabel(i));
            }

            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.add(rs.getObject(i));
                }
                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Schedule ID"," Subject", "Section", "Start Time", "End Time"));
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

                Vector<String> columnNames = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    columnNames.add(md.getColumnLabel(i));
                }

                Vector<Vector<Object>> data = new Vector<>();
                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    data.add(row);
                }

                return new DefaultTableModel(data, columnNames);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "First Name", "Last Name"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

}
