package Login;

import Admin.AdminModel;
import Connection.Ticket;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;

public class LoginDAOImpl implements LoginDAO {
    
    private Connection conn;
    private ResultSet rs;
    
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
                    String fName = rs.getString("fname");
                    String mName = rs.getString("mname");
                    String lName = rs.getString("lname");
                    String conNum = rs.getString("contact_num");
                    String email = rs.getString("email");
                    String sx = rs.getString("sex");
                    Date bday = rs.getDate("birthdate");
                    byte[] image = rs.getBytes("image");
                    String coll = rs.getString("college");
                    if (storedHash.equals(pass)) {
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
            
        } catch (SQLException err) {
            JOptionPane.showMessageDialog(null, err.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
        
        return admin;
    }
    
}
