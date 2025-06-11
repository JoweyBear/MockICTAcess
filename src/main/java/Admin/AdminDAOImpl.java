package Admin;

import Connection.Ticket;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AdminDAOImpl implements AdminDAO {

    private Connection conn;
    private ResultSet rs;

    public AdminDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchAll() {
        try {
            String sql = "SELECT u.user_id, u.fname, u.lname, u.email, a.username "
                    + "FROM user u "
                    + "JOIN auth a ON u.user_id = a.user_id "
                    + "WHERE u.role = 'admin'";

            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
            int columnCount = md.getColumnCount();

            // Build column headers
            Vector<String> columnNames = new Vector<>();
            for (int i = 1; i <= columnCount; i++) {
                columnNames.add(md.getColumnLabel(i));
            }

            // Build rows, decrypting on the fly
            Vector<Vector<Object>> data = new Vector<>();
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                for (int i = 1; i <= columnCount; i++) {
                    String col = md.getColumnLabel(i);
                    Object val = rs.getObject(i);
//                    if (val != null && ("First Name".equals(col) || "Middle Name".equals(col) || "Last Name".equals(col) || "Health Condition".equals(col) || "Contact #".equals(col))) {
//                        val = de.decrypt(val.toString());
//                    }
                    row.add(val);
                }
                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        Vector<String> columns = new Vector<>(List.of("ID", "First Name", "Middle Name", "Last Name", "Sex",
                "Date of Birth", "Brgy", "Code", "4Ps", "Indigent", "Highest Educ Att", "Ethnicity", "Net Income",
                "Occupation", "Health Condition", "House Status", "House Condition", "Contact #", "Latitude", "Longitude"));
        return new DefaultTableModel(new Vector<>(), columns);

    }

    @Override
    public void save(AdminModel admin) {
        try {
            // Step 1: Insert into user table
            String userSql = "INSERT INTO user (role, fname, mname, lname, contact_num, email, sex, birthdate, college) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPs = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS);
            userPs.setString(1, "admin");
            userPs.setString(2, "Anna");
            userPs.setString(3, "M.");
            userPs.setString(4, "Lopez");
            userPs.setString(5, "09981234567");
            userPs.setString(6, "anna.admin@email.com");
            userPs.setString(7, "Female");
            userPs.setDate(8, java.sql.Date.valueOf("1990-03-15"));
            userPs.setString(9, "Management");
            userPs.executeUpdate();

            // Get the generated user_id
            ResultSet rs = userPs.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1);
            }

// Step 2: Insert into auth table
            String username = "admin_anna";
            String plainPassword = "adminpass123"; // You'll hash this before storing
            String passwordHash = plainPassword; // Implement your hash function

            String authSql = "INSERT INTO auth (user_id, username, password_hash) VALUES (?, ?, ?)";
            PreparedStatement authPs = conn.prepareStatement(authSql);
            authPs.setInt(1, userId);
            authPs.setString(2, username);
            authPs.setString(3, passwordHash);
            authPs.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(AdminModel admin) {
        int userId = 1;
        try {
// Update user table
            String updateUserSql = "UPDATE user SET fname = ?, lname = ?, email = ?, contact_num = ? WHERE user_id = ?";
            PreparedStatement updateUserPs = conn.prepareStatement(updateUserSql);
            updateUserPs.setString(1, "UpdatedFirstName");
            updateUserPs.setString(2, "UpdatedLastName");
            updateUserPs.setString(3, "updated@email.com");
            updateUserPs.setString(4, "09998887777");
            updateUserPs.setInt(5, userId);  // use the correct user_id
            updateUserPs.executeUpdate();

// Update auth table (username)
            String updateAuthSql = "UPDATE auth SET username = ? WHERE user_id = ?";
            PreparedStatement updateAuthPs = conn.prepareStatement(updateAuthSql);
            updateAuthPs.setString(1, "updatedUsername");
            updateAuthPs.setInt(2, userId);  // same user_id
            updateAuthPs.executeUpdate();

            JOptionPane.showMessageDialog(null, "Admin Information Updated!");

        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @Override
    public void delete(String admin_id) {
        try {
            String sql = "Delete from admin where staff_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, admin_id);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Admin Deleted!");
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }

    }

    @Override
    public void fecthFingerprint() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
