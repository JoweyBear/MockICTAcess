package Admin;

import Connection.Ticket;
import Utilities.FingerprintCapture;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Date;
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
import org.mindrot.jbcrypt.BCrypt;

public class AdminDAOImpl implements AdminDAO {

    private Connection conn;
    private ResultSet rs;

    public AdminDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchAll() {
        try {
            String sql = "SELECT u.user_id AS 'ID', u.fname AS 'First Name', u.mname AS 'Middle Name', "
                    + "u.lname AS 'Last Name', u.email AS 'Email', a.username AS 'Username', "
                    + "CASE WHEN u.is_active = 1 THEN 'Active' ELSE 'Inactive' END AS 'Status' "
                    + "FROM user u "
                    + "JOIN auth a ON u.user_id = a.user_id "
                    + "WHERE u.role = 'admin'";

            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(sql);
            ResultSetMetaData md = (ResultSetMetaData) rs.getMetaData();
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
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return empty default if something fails
        Vector<String> columns = new Vector<>(List.of("ID", "First Name", "Middle Name", "Last Name", "Email", "Username", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public void save(AdminModel admin) {
        try {
            String userSql = "INSERT INTO user (user_id, role, fname, mname, lname, contact_num, email, sex, birthdate, image, college) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, admin.getStaff_id());
            userPs.setString(2, "admin");
            userPs.setString(3, admin.getStFname());
            userPs.setString(4, admin.getStMname());
            userPs.setString(5, admin.getStLname());
            userPs.setString(6, admin.getConNum());
            userPs.setString(7, admin.getEmail());
            userPs.setString(8, admin.getSx());
            userPs.setDate(9, new java.sql.Date(admin.getBday().getTime()));
            userPs.setBytes(10, admin.getImage());
            userPs.setString(11, admin.getCollge());
            userPs.executeUpdate();

            String orgPass = admin.getPass();
            String hash = BCrypt.hashpw(orgPass, BCrypt.gensalt());
            String authSql = "INSERT INTO auth (user_id, username, hash) VALUES (?, ?, ?)";
            PreparedStatement authPs = conn.prepareStatement(authSql);
            authPs.setString(1, admin.getStaff_id());
            authPs.setString(2, admin.getUsername());
            authPs.setString(3, hash);
            authPs.executeUpdate();

            // Save fingerprint
//        DPFPTemplate template = fingerprintCapture.getTemplate();
//        if (template != null) {
//            byte[] fingerprintData = template.serialize();
//
            String updateFingerprintSql = "INSERT into identification (user_id, finger_template) VALUES (?, ?)";
            PreparedStatement fingerprintPs = conn.prepareStatement(updateFingerprintSql);
            fingerprintPs.setString(1, admin.getStaff_id());
            fingerprintPs.setBytes(2, admin.getFingerprint());
            fingerprintPs.executeUpdate();
//
//            System.out.println("Fingerprint saved successfully.");
//        } else {
//            System.out.println("No fingerprint template captured.");
//        }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void update(AdminModel admin) {
        try {
            String userSql = "UPDATE user SET fname = ?, mname = ?, lname = ?, contact_num = ?, email = ?, sex = ?, birthdate = ?, image = ?, college = ? WHERE user_id = ?";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, admin.getStFname());
            userPs.setString(2, admin.getStMname());
            userPs.setString(3, admin.getStLname());
            userPs.setString(4, admin.getConNum());
            userPs.setString(5, admin.getEmail());
            userPs.setString(6, admin.getSx());
            userPs.setDate(7, new java.sql.Date(admin.getBday().getTime()));
            userPs.setBytes(8, admin.getImage());
            userPs.setString(9, admin.getCollge());
            userPs.setString(10, admin.getStaff_id());
            userPs.executeUpdate();

            String authSql = "UPDATE auth SET username = ?, hash = ? WHERE user_id = ?";
            PreparedStatement authPs = conn.prepareStatement(authSql);
            authPs.setString(1, admin.getUsername());
            authPs.setString(2, admin.getPass());
            authPs.setString(3, admin.getStaff_id());
            authPs.executeUpdate();

//        // Step 3: Update or insert fingerprint
//        DPFPTemplate template = fingerprintCapture.getTemplate();
//        if (template != null) {
//            byte[] fingerprintData = template.serialize();
//
            String checkSql = "SELECT id FROM identification WHERE user_id = ?";
            PreparedStatement checkPs = conn.prepareStatement(checkSql);
            checkPs.setString(1, admin.getStaff_id());
            ResultSet rs = checkPs.executeQuery();
//
            if (rs.next()) {
                String updateFingerprintSql = "UPDATE identification SET fingerprint_template = ? WHERE user_id = ?";
                PreparedStatement updateFpPs = conn.prepareStatement(updateFingerprintSql);
                updateFpPs.setBytes(1, admin.getFingerprint());
                updateFpPs.setString(2, admin.getStaff_id());
                updateFpPs.executeUpdate();
                System.out.println("Fingerprint updated.");
            } else {
                String insertFpSql = "INSERT INTO identification (user_id, fingerprint_template) VALUES (?, ?)";
                PreparedStatement insertFpPs = conn.prepareStatement(insertFpSql);
                insertFpPs.setString(1, admin.getStaff_id());
                insertFpPs.setBytes(2, admin.getFingerprint());
                insertFpPs.executeUpdate();
                System.out.println("Fingerprint inserted.");
            }
//        } else {
//            System.out.println("No fingerprint template captured.");
//        }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(String admin_id) {
        try {
            String sql = "UPDATE user SET is_active = 0 WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, admin_id);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Admin Deleted!");
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }

    }

}
