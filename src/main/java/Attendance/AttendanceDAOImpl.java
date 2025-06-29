package Attendance;

import Connection.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AttendanceDAOImpl implements AttendanceDAO {

    Connection conn;
    ResultSet rs;

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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteClassSched(int cs_id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
