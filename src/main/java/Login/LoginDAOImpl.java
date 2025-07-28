package Login;

import Admin.AdminModel;
import Connection.Ticket;
import Fingerprint.FingerprintModel;
import Utilities.Encryption;
import java.io.IOException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class LoginDAOImpl implements LoginDAO {

    private Connection conn;
    private ResultSet rs;
    Encryption en = new Encryption();

    public LoginDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public AdminModel adminLogin(String user, String pass) {
        AdminModel admin = null;
        try {
            if (user != null && !user.isEmpty() && pass != null && !pass.isEmpty()) {
                String sql = "SELECT a.user_id, a.hash, u.fname, u.mname, u.contact_num, u.email, u.sex, u.birthdate, u.image, u.college, u.lname "
                        + "FROM auth a JOIN user u ON a.user_id = u.user_id "
                        + "WHERE a.username = ? AND u.role = 'admin'";
                PreparedStatement stmt = conn.prepareStatement(sql);
                stmt.setString(1, user);
                rs = stmt.executeQuery();

                if (rs.next()) {
                    String storedHash = rs.getString("hash");

//                    if (storedHash.equals(pass)) {
                    if (BCrypt.checkpw(pass, storedHash)) {
                        String fName = en.decrypt(rs.getString("fname"));
                        String mName = en.decrypt(rs.getString("mname"));
                        String lName = en.decrypt(rs.getString("lname"));
                        String conNum = en.decrypt(rs.getString("contact_num"));
                        String email = en.decrypt(rs.getString("email"));
                        String sx = rs.getString("sex");
                        Date bday = rs.getDate("birthdate");
                        byte[] image = rs.getBytes("image");
                        String coll = rs.getString("college");

                        admin = new AdminModel();
                        admin.setStFname(fName);
                        admin.setStMname(mName);
                        admin.setStLname(lName);
                        admin.setSx(sx);
                        admin.setConNum(conNum);
                        admin.setBday(bday);
                        admin.setCollge(coll);
                        admin.setEmail(email);
                        admin.setImage(image);

                        System.out.println("Welcome, " + fName + " " + lName + "!");
                    } else {
                        System.out.println("Invalid password.");
                    }
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        return admin;
    }

    @Override
    public List<AdminModel> verifyAdminLogin() {
        List<AdminModel> admins = new ArrayList<>();

        String sql = "SELECT u.user_id, u.fname, u.mname, u.lname, "
                + "u.contact_num, u.email, u.sex, u.birthdate, u.image, u.college, "
                + "i.fingerprint_template "
                + "FROM user u "
                + "JOIN identification i ON u.user_id = i.user_id "
                + "WHERE u.role = 'admin' AND i.fingerprint_template IS NOT NULL";

        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fName = rs.getString("fname");
                String mName = rs.getString("mname");
                String lName = rs.getString("lname");
                String conNum = rs.getString("contact_num");
                String email = rs.getString("email");
                String sx = rs.getString("sex");
                Date bday = rs.getDate("birthdate");
                byte[] image = rs.getBytes("image");
                String coll = rs.getString("college");
                byte[] fingerprint = rs.getBytes("fingerprint_template");

                AdminModel admin = new AdminModel();
                admin.setStFname(fName);
                admin.setStMname(mName);
                admin.setStLname(lName);
                admin.setSx(sx);
                admin.setConNum(conNum);
                admin.setBday(bday);
                admin.setCollge(coll);
                admin.setEmail(email);
                admin.setImage(image);
                admin.setFingerprint(fingerprint);

                System.out.println("Welcome, " + fName + " " + lName + "!");
                System.out.println("Loaded fingerprint size: " + fingerprint.length);

                admins.add(admin);
            }

        } catch (SQLException ex) {
            Logger.getLogger(LoginDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        return admins;
    }

    @Override
    public AdminModel AdminInfo(FingerprintModel info) {
        AdminModel admin = new AdminModel();
        admin.setStaff_id(info.getUser_id());
        admin.setStFname(info.getFname());
        admin.setStLname(info.getLname());
        admin.setFingerprint(info.getTemplate());
        admin.setFingerprintImage(info.getTemplate_image());
        admin.setSx(info.getSex());
        admin.setConNum(info.getContact_num());
        admin.setBday(info.getBirthdate());
        admin.setCollge(info.getCollege());
        admin.setEmail(info.getEmail());
        admin.setImage(info.getImage());

        return admin;
    }

}
