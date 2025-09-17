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
    public DefaultTableModel displayOverAllDificiencies() {
        String sql = "WITH summary_count AS "
                + "(SELECT user_id, SUM(CASE WHEN status = 'Late' THEN 1 ELSE 0 END) AS 'lateCount', "
                + "SUM(WHEN status = 'Absent' AND time_in IS NULL AND time_out IS NULL THEN 1 ELSE 0 END) AS 'absentCount' "
                + "FROM attendance GROUP BY user_id) "
                + "SELECT sc.user_id, s.fname, s.lname, "
                + "si.year, si.section, si.track, "
                + "sc.lateCount AS 'late_count', "
                + "sc.absentCount AS 'absent_count' "
                + "FROM summary_count sc "
                + "JOIN user s ON sc.user_id = s.user_id "
                + "JOIN student_info si ON sc.user_id = si.user_id "
                + "WHERE s.role = 'student' AND s.college = ? "
                + "ORDER BY s.lname ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, college);
            rs = ps.executeQuery();

            Vector<String> columnNames = new Vector<>(Arrays.asList(
                    "Student ID", "Student Name", "Year", "Section", "Track",
                    "Late Counts", "Absent Counts", "Risk Status"
            ));
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();

                String fname = de.decrypt(rs.getString("fname") != null ? rs.getString("fname") : "");
                String lname = de.decrypt(rs.getString("lname") != null ? rs.getString("lname") : "");
                String late_count = rs.getString("late_count");
                
                
                String fullName = fname + " " + lname;
                row.add(rs.getString("user_id"));
                row.add(fullName);
                row.add(rs.getString("year"));
                row.add(rs.getString("section"));
                row.add(rs.getString("track"));
                row.add(rs.getString("late_count"));
                row.add(rs.getString("absent_count"));
//                String risk = (lateCount + absentCount > 10) ? "High" : "Low";

                

                data.add(row);

            }
            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(AnalyticsDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<String> columns = new Vector<>(Arrays.asList("Student ID", "Student Name", "Year", "Section", "Track", 
                    "Late Counts", "Absent Counts", "Risk Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

}
