package Faculty;

import Connection.Ticket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class FacultyDAOImpl implements FacultyDAO {

    Connection conn;
    ResultSet rs;

    public FacultyDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchAll() {
        try {
            String sql = "SELECT u.user_id AS 'ID', u.college AS 'College', u.fname AS 'First Name', u.mname AS 'Middle Name', "
                    + "u.lname AS 'Last Name', u.contact_num AS 'Contact Number', u.sex AS 'Sex', u.birthdate AS 'Birthdate', "
                    + "u.barangay AS 'Barangay', u.municipality AS 'Municipality',"
                    + " u.email AS 'Email', a. AS 'Section', a.year AS 'Year', "
                    + "CASE WHEN u.is_active = 1 THEN 'Active' ELSE 'Inactive' END "
                    + "FROM user u "
                    + "JOIN faculty_info a ON u.user_id = a.user_id "
                    + "WHERE u.role = 'faculty'";

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
            Logger.getLogger(FacultyDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<String> columns = new Vector<>(Arrays.asList("ID", "College", "Position", "First Name", "Middle Name", "Last Name", "Sex", "Birthdate", "Contact Number", "Email", "Barangay", "Municipality"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public void save(FacultyModel faculty) {
        try {
            String userSql = "INSERT INTO user (user_id, role, fname, mname, lname, contact_num, email, barangay, municipality, sex, birthdate, image, college) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, faculty.getFaculty_id());
            userPs.setString(2, "student");
            userPs.setString(3, faculty.getFname());
            userPs.setString(4, faculty.getMname());
            userPs.setString(5, faculty.getLname());
            userPs.setString(6, faculty.getCntctNmber());
            userPs.setString(7, faculty.getEmail());
            userPs.setString(8, faculty.getBrgy());
            userPs.setString(9, faculty.getMunicipal());
            userPs.setString(8, faculty.getSx());
            userPs.setDate(9, new java.sql.Date(faculty.getBday().getTime()));
            userPs.setBytes(10, faculty.getImage());
            userPs.setString(11, faculty.getCollege());
            userPs.executeUpdate();

            String infoSql = "INSERT INTO student_info(user_id, position) VALUES (?, ?)";
            PreparedStatement infoPs = conn.prepareStatement(infoSql);
            infoPs.setString(1, faculty.getFaculty_id());
            infoPs.setString(2, faculty.getPosition());
            infoPs.executeUpdate();

            String updateFingerprintSql = "INSERT into identification (user_id, finger_template, fingerprint_image) VALUES (?, ?)";
            PreparedStatement fingerprintPs = conn.prepareStatement(updateFingerprintSql);
            fingerprintPs.setString(1, faculty.getFaculty_id());
            fingerprintPs.setBytes(2, faculty.getFingerprint());
            fingerprintPs.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(FacultyDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }    }

    @Override
    public void update(FacultyModel faculty) {
        try {
            String userSql = "UPDATE user SET fname = ?, mname = ?, lname = ?, contact_num = ?, email = ?, barangay = ?, municipality = ?, sex = ?, birthdate = ?, image = ?, college = ? WHERE user_id = ?";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1,  faculty.getFname());
            userPs.setString(2,  faculty.getMname());
            userPs.setString(3,  faculty.getLname());
            userPs.setString(4,  faculty.getCntctNmber());
            userPs.setString(5,  faculty.getEmail());
            userPs.setString(6,  faculty.getBrgy());
            userPs.setString(7,  faculty.getMunicipal());
            userPs.setString(8,  faculty.getSx());
            userPs.setDate(9, new java.sql.Date( faculty.getBday().getTime()));
            userPs.setBytes(10,  faculty.getImage());
            userPs.setString(11,  faculty.getCollege());
            userPs.setString(12,  faculty.getFaculty_id());
            userPs.executeUpdate();

            String infoSql = "UPDATE faculty_info SET position = ? WHERE user_id = ?";
            PreparedStatement infoPs = conn.prepareStatement(infoSql);
            infoPs.setString(1, faculty.getPosition());
            infoPs.setString(2, faculty.getFaculty_id());
            infoPs.executeUpdate();

            if (faculty.getFingerprint() != null && faculty.getFingerprintImage() != null) {
                String checkSql = "SELECT fingerprint_template, fingerprint_image FROM identification WHERE user_id = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, faculty.getFaculty_id());
                rs = checkPs.executeQuery();

                if (rs.next()) {
                    byte[] oldFingerprint = rs.getBytes("fingerprint_template");
                    byte[] oldFingerprintImage = rs.getBytes("fingerprint_image");

                    String historySql = "INSERT INTO fingerprint_history (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement historyPs = conn.prepareStatement(historySql);
                    historyPs.setString(1, faculty.getFaculty_id());
                    historyPs.setBytes(2, oldFingerprint);
                    historyPs.setBytes(3, oldFingerprintImage);
                    historyPs.executeUpdate();

                    String updateFingerprintSql = "UPDATE identification SET fingerprint_template = ?, fingerprint_image = ? WHERE user_id = ?";
                    PreparedStatement updateFpPs = conn.prepareStatement(updateFingerprintSql);
                    updateFpPs.setBytes(1, faculty.getFingerprint());
                    updateFpPs.setBytes(2, faculty.getFingerprintImage());
                    updateFpPs.setString(3, faculty.getFaculty_id());
                    updateFpPs.executeUpdate();

                    System.out.println("Fingerprint and image updated, history saved.");
                } else {
                    String insertFpSql = "INSERT INTO identification (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement insertFpPs = conn.prepareStatement(insertFpSql);
                    insertFpPs.setString(1, faculty.getFaculty_id());
                    insertFpPs.setBytes(2, faculty.getFingerprint());
                    insertFpPs.setBytes(3, faculty.getFingerprintImage());
                    insertFpPs.executeUpdate();

                    System.out.println("Fingerprint and image inserted.");
                }
            } else {
                System.out.println("No fingerprint data provided — only user details updated.");
            }

        } catch (SQLException ex) {
            Logger.getLogger(FacultyDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }    }

    @Override
    public void delete(String stud_id) {
        try {
            String sql = "UPDATE user SET is_active = 0 WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, stud_id);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Student Deleted!");
        } catch (SQLException ex) {
            Logger.getLogger(FacultyDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }    }

    @Override
    public FacultyModel facultyView(String faculty_id) {
        FacultyModel faculty = new FacultyModel();
        try {
            String sql = "SELECT u.image, i.fingerprint_template, i.fingerprint_image FROM user u "
                    + "LEFT JOIN identification i ON u.user_id = i.user_id WHERE u.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, faculty_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("image");
                byte[] fingerprintBytes = rs.getBytes("fingerprint_template");
                byte[] fngrprntImgBytes = rs.getBytes("fingerprint_image");

                faculty.setFingerprintData(fingerprintBytes);
                faculty.setImageData(imageBytes);
                faculty.setFngrprntImageData(fngrprntImgBytes);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacultyDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return faculty;
        }

}
