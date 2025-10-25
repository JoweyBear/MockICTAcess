package Admin;

import Connection.Ticket;
import Utilities.Encryption;
import com.mysql.cj.jdbc.result.ResultSetMetaData;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.Arrays;
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
    Encryption de = new Encryption();

    public AdminDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchAll() {
        try {
            String sql = "SELECT u.user_id AS 'ID', u.college AS 'College', u.fname AS 'First Name', u.mname AS 'Middle Name', "
                    + "u.lname AS 'Last Name', u.contact_num AS 'Contact Number', u.sex AS 'Sex', u.birthdate AS 'Birthdate', "
                    + "u.barangay AS 'Barangay', u.municipality AS 'Municipality', u.email AS 'Email', a.username AS 'Username', "
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
                    String col = md.getColumnLabel(i);
                    Object val = rs.getObject(i);

                    if (val != null && Arrays.asList(
                            "First Name", "Middle Name", "Last Name",
                            "Municipality", "Barangay", "Contact Number"
                    ).contains(col)) {
                        try {
                            val = de.decrypt(val.toString());
                        } catch (Exception ex) {
                            System.err.println("Decryption failed for " + col + ": " + ex.getMessage());
                        }
                    }

                    row.add(val);
                }
                data.add(row);
            }

            return new DefaultTableModel(data, columnNames);

        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<String> columns = new Vector<>(Arrays.asList(
                "ID", "College", "First Name", "Middle Name", "Last Name", "Contact Number",
                "Sex", "Birthdate", "Barangay", "Municipality", "Email", "Username", "Status"
        ));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public boolean save(AdminModel admin) {

        boolean saved = false;
        try {
            String userSql = "INSERT INTO user (user_id, role, fname, mname, lname, contact_num, email, "
                    + "barangay, sex, birthdate, image, college) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, admin.getStaff_id());
            userPs.setString(2, "admin");
//            userPs.setString(3, admin.getStFname());
//            userPs.setString(4, admin.getStMname());
//            userPs.setString(5, admin.getStLname());
//            userPs.setString(6, admin.getConNum());
            userPs.setString(3, de.encrypt(admin.getStFname()));
            userPs.setString(4, de.encrypt(admin.getStMname()));
            userPs.setString(5, de.encrypt(admin.getStLname()));
            userPs.setString(6, de.encrypt(admin.getConNum()));
            userPs.setString(7, admin.getEmail());
//            userPs.setString(8, admin.getBarangay());
//            userPs.setString(9, admin.getMunicipal());
            userPs.setString(8, de.encrypt(admin.getAddress()));
//            userPs.setString(9, de.encrypt(admin.getMunicipal()));
            userPs.setString(9, admin.getSx());
            userPs.setDate(10, new java.sql.Date(admin.getBday().getTime()));
            userPs.setBytes(11, admin.getImage());
            userPs.setString(12, admin.getCollge());
            userPs.execute();

            String orgPass = admin.getPass();
            String hash = BCrypt.hashpw(orgPass, BCrypt.gensalt());
            String authSql = "INSERT INTO auth (user_id, username, hash) VALUES (?, ?, ?)";
            PreparedStatement authPs = conn.prepareStatement(authSql);
            authPs.setString(1, admin.getStaff_id());
            authPs.setString(2, admin.getUsername());
            authPs.setString(3, hash);
            authPs.execute();

            String updateFingerprintSql = "INSERT into identification (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
            PreparedStatement fingerprintPs = conn.prepareStatement(updateFingerprintSql);
            fingerprintPs.setString(1, admin.getStaff_id());
            fingerprintPs.setBytes(2, admin.getFingerprint());
            fingerprintPs.setBytes(3, admin.getFingerprintImage());
            fingerprintPs.execute();

            saved = true;
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return saved;
    }

    @Override
    public boolean update(AdminModel admin) {
        boolean update = false;
        try {
            String userSql = "UPDATE user SET fname = ?, mname = ?, lname = ?, contact_num = ?, email = ?, barangay = ?, sex = ?, birthdate = ?, image = ?, college = ? WHERE user_id = ?";
            PreparedStatement userPs = conn.prepareStatement(userSql);
//            userPs.setString(1, admin.getStFname());
//            userPs.setString(2, admin.getStMname());
//            userPs.setString(3, admin.getStLname());
            userPs.setString(1, de.encrypt(admin.getStFname()));
            userPs.setString(2, de.encrypt(admin.getStMname()));
            userPs.setString(3, de.encrypt(admin.getStLname()));
//            userPs.setString(4, admin.getConNum());
            userPs.setString(4, de.encrypt(admin.getConNum()));
            userPs.setString(5, admin.getEmail());
            userPs.setString(6, de.encrypt(admin.getAddress()));
//            userPs.setString(7, de.encrypt(admin.getMunicipal()));
//            userPs.setString(6, admin.getBarangay());
//            userPs.setString(7, admin.getMunicipal());
            userPs.setString(7, admin.getSx());
            userPs.setDate(8, new java.sql.Date(admin.getBday().getTime()));
            userPs.setBytes(9, admin.getImage());
            userPs.setString(10, admin.getCollge());
            userPs.setString(11, admin.getStaff_id());
            userPs.executeUpdate();

            PreparedStatement authPs;

            if (admin.getPass() != null && !admin.getPass().isEmpty()) {
                String authSql = "UPDATE auth SET username = ?, hash = ? WHERE user_id = ?";
                authPs = conn.prepareStatement(authSql);
                authPs.setString(1, admin.getUsername());
                authPs.setString(2, admin.getPass());
                authPs.setString(3, admin.getStaff_id());
            } else {
                String authSql = "UPDATE auth SET username = ? WHERE user_id = ?";
                authPs = conn.prepareStatement(authSql);
                authPs.setString(1, admin.getUsername());
                authPs.setString(2, admin.getStaff_id());
            }

            authPs.executeUpdate();

            if (admin.getFingerprint() != null && admin.getFingerprintImage() != null) {
                String checkSql = "SELECT fingerprint_template, fingerprint_image FROM identification WHERE user_id = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, admin.getStaff_id());
                rs = checkPs.executeQuery();

                if (rs.next()) {
                    byte[] oldFingerprint = rs.getBytes("fingerprint_template");
                    byte[] oldFingerprintImage = rs.getBytes("fingerprint_image");

                    String historySql = "INSERT INTO fingerprint_history (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement historyPs = conn.prepareStatement(historySql);
                    historyPs.setString(1, admin.getStaff_id());
                    historyPs.setBytes(2, oldFingerprint);
                    historyPs.setBytes(3, oldFingerprintImage);
                    historyPs.executeUpdate();

                    String updateFingerprintSql = "UPDATE identification SET fingerprint_template = ?, fingerprint_image = ? WHERE user_id = ?";
                    PreparedStatement updateFpPs = conn.prepareStatement(updateFingerprintSql);
                    updateFpPs.setBytes(1, admin.getFingerprint());
                    updateFpPs.setBytes(2, admin.getFingerprintImage());
                    updateFpPs.setString(3, admin.getStaff_id());
                    updateFpPs.executeUpdate();

                    System.out.println("Fingerprint and image updated, history saved.");
                } else {
                    String insertFpSql = "INSERT INTO identification (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement insertFpPs = conn.prepareStatement(insertFpSql);
                    insertFpPs.setString(1, admin.getStaff_id());
                    insertFpPs.setBytes(2, admin.getFingerprint());
                    insertFpPs.setBytes(3, admin.getFingerprintImage());
                    insertFpPs.executeUpdate();

                    System.out.println("Fingerprint and image inserted.");
                }
            } else {
                System.out.println("No fingerprint data provided - only user details updated.");
            }
            update = true;
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return update;
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

    @Override
    public AdminModel view(String admin_id) {
        AdminModel admin = new AdminModel();
        try {
            String sql = "SELECT u.image, i.fingerprint_template, i.fingerprint_image FROM user u "
                    + "LEFT JOIN identification i ON u.user_id = i.user_id WHERE u.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, admin_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("image");
                byte[] fingerprintBytes = rs.getBytes("fingerprint_template");
                byte[] fngrprntImgBytes = rs.getBytes("fingerprint_image");

                admin.setFingerprintData(fingerprintBytes);
                admin.setImageData(imageBytes);
                admin.setFngrprntImageData(fngrprntImgBytes);
//                System.out.println("Byte array has data. Size: " + fngrprntImgBytes.length + " bytes");

            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return admin;
    }

    @Override
    public boolean isUsernameTaken(String username) {
        try {
            String sql = "SELECT COUNT(*) FROM auth WHERE username = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, username);
            rs = ps.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0; 
            }
        } catch (SQLException ex) {
            Logger.getLogger(AdminDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

}
