package Attendance;

import Connection.Ticket;
import Utilities.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class AttendanceDAOImpl implements AttendanceDAO {

    Connection conn;
    ResultSet rs;
    private String college = GlobalVar.loggedInAdmin.getCollge();

    public AttendanceDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public void saveRoom(AttModel att) {
        try {
            String sql = "INSERT INTO room (name, college, building, floor_level, type, description) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, att.getName());
            ps.setString(2, att.getCollege());
            ps.setString(3, att.getBuilding());
            ps.setString(4, att.getFlr_lvl());
            ps.setString(5, att.getType());
            ps.setString(6, att.getDesc());
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void saveClassSched(AttModel att) {
        try {
            String sql = "INSERT INTO class_schedule(class_type, day, time_start, time_end, subject, faculty_user_id, room_id) VALUES(?, ?, ?, ?, ?, ? ,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, att.getClass_type());
            ps.setString(2, att.getDay());
            ps.setTime(3, java.sql.Time.valueOf(att.getTime_strt()));
            ps.setTime(4, java.sql.Time.valueOf(att.getTime_end()));
            ps.setString(5, att.getSubject());
            ps.setString(6, att.getFaculty_id());
            ps.setInt(7, att.getRm_id());
            ps.execute();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateRoom(AttModel att) {
        try {
            String sql = "UPDATE room SET name = ?, college = ?, building = ?, floor_level = ?, type = ?, description = ? WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, att.getName());
            ps.setString(2, att.getCollege());
            ps.setString(3, att.getBuilding());
            ps.setString(4, att.getFlr_lvl());
            ps.setString(5, att.getType());
            ps.setString(6, att.getDesc());
            ps.setInt(7, att.getRm_id());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void updateClassSched(AttModel att) {
        try {
            String sql = "UPDATE class_schedule SET class_type = ?, day = ?, time_start = ?, time_end = ?, subject = ?, faculty_user_id = ?, room_id = ? WHERE cs_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, att.getClass_type());
            ps.setString(2, att.getDay());
            ps.setTime(3, java.sql.Time.valueOf(att.getTime_strt()));
            ps.setTime(4, java.sql.Time.valueOf(att.getTime_end()));
            ps.setString(5, att.getSubject());
            ps.setString(6, att.getFaculty_id());
            ps.setInt(7, att.getRm_id());
            ps.setInt(8, att.getCs_id());
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public void deleteRoom(int room_id) {
        try {
            String sql = "DELETE FROM room WHERE room_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, room_id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void deleteClassSched(int cs_id) {
        try {
            String sql = "DELETE FROM class_schedule WHERE cs_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, cs_id);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @Override
    public DefaultTableModel getByCSId(int csId) {
        try {
            String sql = "SELECT a.cs_id AS 'Class Schedule ID', "
                    + "CONCAT(u.fname, ' ', u.mname, ' ', u.lname) AS 'Student Name', a.student_user_id AS 'Student ID', "
                    + "cs.subject AS 'Subject', CONCAT(f.fname, ' ', f.mname, ' ', f.lname) AS 'Faculty', a.att_date_time AS 'Date', a.method AS 'Method', a.status AS 'Status' "
                    + "FROM attendance a "
                    + "JOIN class_schedule cs ON a.cs_id = cs.cs_id "
                    + "JOIN user u ON a.student_user_id = u.user_id "
                    + "JOIN user f ON cs.faculty_user_id = f.user_id "
                    + "WHERE a.cs_id = ? AND cs.college = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, csId);
            ps.setString(2, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Class Schedule ID", "Student ID", "Student Name", "Subject", "Faculty", "Date", "Method", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getByRoomId(int roomId) {
        try {
            String sql = "SELECT cs.cs_id AS 'Class Schedule ID', cs.subject AS 'Subject', cs.day AS 'Day', "
                    + "cs.time_start AS 'Time Start', cs.time_end AS 'Time End' FROM class_schedule cs "
                    + "WHERE cs.room_id = ? AND cs.college = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, roomId);
            ps.setString(2, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Class Schedule ID", "Subject", "Day", "Time Start", "Time End"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getAll() {
        try {
            String sql = "SELECT a.attendance_id AS 'Attendance ID', a.cs_id AS 'Class Schedule ID', "
                    + "CONCAT(u.fname, ' ', u.mname, ' ', u.lname) AS 'Student Name', a.student_user_id AS 'Student ID', "
                    + "cs.subject AS 'Subject', CONCAT(f.fname, ' ', f.mname, ' ', f.lname) AS 'Faculty', "
                    + "a.att_date_time AS 'Date', a.method AS 'Method', a.status AS 'Status' "
                    + "FROM attendance a "
                    + "JOIN class_schedule cs ON a.cs_id = cs.cs_id "
                    + "JOIN user u ON a.student_user_id = u.user_id "
                    + "JOIN user f ON cs.faculty_user_id = f.user_id "
                    + "WHERE cs.college = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, college);
            ResultSet rs = ps.executeQuery();
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
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

        Vector<String> columns = new Vector<>(List.of("Attendance ID", "Class Schedule ID", "Student ID", "Student Name", "Subject", "Faculty", "Date", "Method", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getByDayAndTime(String day, LocalTime time) {
        try {
            String sql = "SELECT cs.cs_id AS 'Class Schedule ID', cs.subject AS 'Subject', CONCAT(f.fname, ' ', f.mname, ' ', f.lname) AS 'Faculty Name', "
                    + "cs.room_id AS 'Room ID', cs.time_start AS 'Time Start', cs.time_end AS 'Time End' FROM class_schedule cs "
                    + "JOIN user f ON cs.faculty_user_id = f.user_id "
                    + "WHERE cs.day = ? AND cs.time_start <= ? AND cs.time_end >= ? AND cs.college = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, day);
            ps.setTime(2, Time.valueOf(time));
            ps.setTime(3, Time.valueOf(time));
            ps.setString(4, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Class Schedule ID", "Subject", "Faculty Name", "Room ID", "Time Start", "Time End"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getByFacultyId(int facultyUserId) {
        try {
            String sql = "SELECT cs.cs_id AS 'Class Schedule ID', cs.subject AS 'Subject', cs.day AS 'Day', "
                    + "cs.time_start AS 'Time Start', cs.time_end AS 'Time End' FROM class_schedule cs "
                    + "WHERE cs.faculty_user_id = ? AND cs.college = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, facultyUserId);
            ps.setString(2, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Class Schedule ID", "Subject", "Day", "Time Start", "Time End"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getAllRoom() {
        try {
            String sql = "SELECT room_id AS 'Room ID', name AS 'Room Name', building AS 'Building',"
                    + " floor_level AS 'Floor Level', type AS 'Room Type' "
                    + "FROM room WHERE college = ?;";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Room ID", "Room Name", "Building", "Floor Level", "Room Type"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public DefaultTableModel getAllCS() {
        try {
            String sql = "SELECT "
                    + "cs_id AS 'Class Schedule ID',"
                    + "class_type AS 'Class Type',"
                    + "day AS 'Day',"
                    + "time_start AS 'Time Start',"
                    + "time_end AS 'Time End',"
                    + "subject AS 'Subject',"
                    + "faculty_user_id AS 'Faculty ID',"
                    + "room_id AS 'Room ID'"
                    + "FROM class_schedule WHERE college = ?;";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, college);
            ResultSet rs = ps.executeQuery();
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

        Vector<String> columns = new Vector<>(List.of("Class Schedule ID", "Class Type", "Subject", "Day", "Time Start", "Time End", "Faculty ID", "Room ID"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public boolean addStudentToClassSchedule(int csId, String studentId) {
        boolean added = false;
        try {
            String sql = "INSERT INTO class_student (class_schedule_id, student_user_id) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, csId);
            ps.setString(2, studentId);
            ps.executeUpdate();
            added = true; // ✅ Just assign, no need to redeclare
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return added;
    }

    @Override
    public void removeStudentFromClassSchedule(int csId, String studentId) {
        try {
            String sql = "DELETE FROM class_student WHERE class_schedule_id = ? AND student_user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, csId);
            ps.setString(2, studentId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AttendanceDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
