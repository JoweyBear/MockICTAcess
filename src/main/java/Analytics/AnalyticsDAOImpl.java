package Analytics;

import Connection.Ticket;
import Utilities.Encryption;
import Utilities.GlobalVar;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class AnalyticsDAOImpl implements AnalyticsDAO {

    Connection conn;
    ResultSet rs;
    String college = GlobalVar.loggedInAdmin.getCollge();
    Encryption de = new Encryption();

    public AnalyticsDAOImpl() {
        conn = Ticket.getConn();

    }

    @Override
    public DefaultTableModel displayOverAllAttendance() {
        String sql = "SELECT a.user_id, s.fname, s.lname, "
                + "a.attendance_date_time, a.time_in, a.time_out, a.status, cs.subject, "
                + "si.year, si.section, si.track "
                + "FROM attendance a "
                + "JOIN user s ON a.user_id = s.user_id "
                + "JOIN class_schedule cs ON a.class_schedule_id = cs.cs_id "
                + "JOIN student_info si ON a.user_id = si.user_id "
                + "WHERE role = 'student' AND s.college = ? "
                + "ORDER BY DATE(a.attendance_date_time) DESC, s.lname ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();

            Vector<String> columnNames = new Vector<>(Arrays.asList(
                    "Student ID", "Student Name", "Year", "Section", "Track",
                    "Subject", "Time_In", "Time_Out", "Status", "Date"
            ));
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String fname = de.decrypt(rs.getString("fname") != null ? rs.getString("fname") : "");
                String lname = de.decrypt(rs.getString("lname") != null ? rs.getString("lname") : "");

                String fullName = fname + " " + lname;
                row.add(rs.getString("user_id"));
                row.add(fullName);
                row.add(rs.getString("year"));
                row.add(rs.getString("section"));
                row.add(rs.getString("track"));
                row.add(rs.getString("subject"));
                row.add(rs.getString("time_in"));
                row.add(rs.getString("time_out"));
                row.add(rs.getString("attendance_date_time"));
                row.add(rs.getString("status"));

                data.add(row);

            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(AnalyticsDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track",
                "Subject", "Date", "Time_In", "Time_Out", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

}
