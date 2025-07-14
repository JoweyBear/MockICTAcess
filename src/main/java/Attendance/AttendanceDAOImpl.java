package Attendance;

import Connection.Ticket;
import Utilities.GlobalVar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.time.LocalTime;
import java.util.Arrays;
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
    public boolean saveRoom(AttModel att) {
        boolean saveRm = false;
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
            saveRm = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return saveRm;
    }

    @Override
    public boolean saveClassSched(AttModel att) {
        boolean saveCS = false;
        try {
            String insertSql = "INSERT INTO class_schedule (class_type, day, section, year,  time_start, time_end, subject, faculty_user_id, room_id, college) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, att.getClass_type());
            ps.setString(2, att.getDay());
            ps.setString(3, att.getSection());
            ps.setString(4, att.getYear());
            ps.setTime(5, Time.valueOf(att.getTime_strt()));
            ps.setTime(6, Time.valueOf(att.getTime_end()));
            ps.setString(7, att.getSubject());
            ps.setString(8, att.getFaculty_id());
            ps.setInt(9, att.getRm_id());
            ps.setString(10, att.getCollege());
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            int classScheduleId = -1;
            if (rs.next()) {
                classScheduleId = rs.getInt(1);
            }

            String studentSql = "SELECT s.user_id FROM user s "
                    + "JOIN student_info si ON s.user_id = si.user_id "
                    + "WHERE s.role = 'student' AND si.year = ? AND si.section = ?";
            PreparedStatement studentPs = conn.prepareStatement(studentSql);
            studentPs.setString(1, att.getYear());
            studentPs.setString(2, att.getYear());
            ResultSet studentRs = studentPs.executeQuery();

            String assignSql = "INSERT INTO class_student (class_schedule_id, student_user_id) VALUES (?, ?)";
            PreparedStatement assignPs = conn.prepareStatement(assignSql);
            while (studentRs.next()) {
                assignPs.setInt(1, classScheduleId);
                assignPs.setString(2, studentRs.getString("user_id"));
                assignPs.addBatch();
            }
            assignPs.executeBatch();

            saveCS = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return saveCS;
    }

    @Override
    public boolean updateRoom(AttModel att) {
        boolean updateRm = false;
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

            updateRm = true;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return updateRm;
    }

    @Override
    public boolean updateClassSched(AttModel att) {
        boolean updateCS = false;
        try {
            conn.setAutoCommit(false);

            String updateSchedule = "UPDATE class_schedule SET class_type = ?, day = ?, time_start = ?, time_end = ?, subject = ?, faculty_user_id = ?, room_id = ?, year = ?, section = ? WHERE cs_id = ?";
            PreparedStatement ps1 = conn.prepareStatement(updateSchedule);
            ps1.setString(1, att.getClass_type());
            ps1.setString(2, att.getDay());
            ps1.setTime(3, Time.valueOf(att.getTime_strt()));
            ps1.setTime(4, Time.valueOf(att.getTime_end()));
            ps1.setString(5, att.getSubject());
            ps1.setString(6, att.getFaculty_id());
            ps1.setInt(7, att.getRm_id());
            ps1.setString(8, att.getYear());
            ps1.setString(9, att.getSection());
            ps1.setInt(10, att.getCs_id());
            ps1.executeUpdate();

            String deleteOld = "DELETE FROM class_student WHERE class_schedule_id = ?";
            PreparedStatement ps2 = conn.prepareStatement(deleteOld);
            ps2.setInt(1, att.getCs_id());
            ps2.executeUpdate();

            String selectStudents = "SELECT user_id FROM student_info WHERE year = ? AND section = ?";
            PreparedStatement ps3 = conn.prepareStatement(selectStudents);
            ps3.setString(1, att.getYear());
            ps3.setString(2, att.getSection());
            ResultSet rs = ps3.executeQuery();

            String insertStudent = "INSERT INTO class_student (class_schedule_id, student_user_id) VALUES (?, ?)";
            PreparedStatement ps4 = conn.prepareStatement(insertStudent);

            while (rs.next()) {
                ps4.setInt(1, att.getCs_id());
                ps4.setString(2, rs.getString("user_id"));
                ps4.addBatch();
            }

            ps4.executeBatch();

            conn.commit();
            updateCS = true;
        } catch (SQLException e) {
            try {
                conn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return updateCS;

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

        Vector<String> columns = new Vector<>(Arrays.asList("Class Schedule ID", "Student ID", "Student Name", "Subject", "Faculty", "Date", "Method", "Status"));
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

        Vector<String> columns = new Vector<>(Arrays.asList("Class Schedule ID", "Subject", "Day", "Time Start", "Time End"));
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

        Vector<String> columns = new Vector<>(Arrays.asList("Attendance ID", "Class Schedule ID", "Student ID", "Student Name", "Subject", "Date", "Faculty", "Method", "Status"));
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

        Vector<String> columns = new Vector<>(Arrays.asList("Class Schedule ID", "Subject", "Faculty Name", "Room ID", "Time Start", "Time End"));
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

        Vector<String> columns = new Vector<>(Arrays.asList("Class Schedule ID", "Subject", "Day", "Time Start", "Time End"));
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

        Vector<String> columns = new Vector<>(Arrays.asList("Room ID", "Room Name", "Building", "Floor Level", "Room Type"));
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
                    + "subject AS 'Subject', "
                    + "section AS 'Section', "
                    + "year AS 'Year', "
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

        Vector<String> columns = new Vector<>(Arrays.asList("Class Schedule ID", "Class Type", "Subject", "Section", "Year", "Day", "Time Start", "Time End", "Faculty ID", "Room ID"));
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
            added = true;
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
