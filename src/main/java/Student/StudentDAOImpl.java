package Student;

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

public class StudentDAOImpl implements StudentDAO {

    Connection conn;
    ResultSet rs;

    public StudentDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public DefaultTableModel fetchAll() {
        try {
            String sql = "SELECT u.user_id AS 'ID', u.college AS 'College' , a.section AS 'Section', a.year AS 'Year', "
                    + "u.fname AS 'First Name', u.mname AS 'Middle Name', u.lname AS 'Last Name', "
                    + "u.sex AS 'Sex', u.birthdate AS 'Birthdate', u.contact_num AS 'Contact Number', "
                    + "u.email AS 'Email', u.barangay AS 'Barangay', u.municipality AS 'Municipality',"
                    + "CASE WHEN u.is_active = 1 THEN 'Active' ELSE 'Inactive' END AS 'Status' "
                    + "FROM user u "
                    + "JOIN student_info a ON u.user_id = a.user_id "
                    + "WHERE u.role = 'student'";

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
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        Vector<String> columns = new Vector<>(List.of("ID", "College", "Year", "Section", "First Name", "Middle Name", "Last Name", "Sex", "Birthdate", "Contact Number", "Email", "Barangay", "Municipality"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public boolean save(StudentModel student) {
        boolean save = false;
        try {
            String userSql = "INSERT INTO user (user_id, role, fname, mname, lname, contact_num, email, barangay, municipality, sex, birthdate, image, college) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, student.getStud_id());
            userPs.setString(2, "student");
            userPs.setString(3, student.getFname());
            userPs.setString(4, student.getMname());
            userPs.setString(5, student.getLname());
            userPs.setString(6, student.getCntctNmber());
            userPs.setString(7, student.getEmail());
            userPs.setString(8, student.getBrgy());
            userPs.setString(9, student.getMunicipal());
            userPs.setString(10, student.getSx());
            userPs.setDate(11, new java.sql.Date(student.getBday().getTime()));
            userPs.setBytes(12, student.getImage());
            userPs.setString(13, student.getCollege());
            userPs.executeUpdate();

            String infoSql = "INSERT INTO student_info(user_id, year, section) VALUES (?, ?, ?)";
            PreparedStatement infoPs = conn.prepareStatement(infoSql);
            infoPs.setString(1, student.getStud_id());
            infoPs.setString(2, student.getYear());
            infoPs.setString(3, student.getSection());
            infoPs.executeUpdate();

            String updateFingerprintSql = "INSERT into identification (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
            PreparedStatement fingerprintPs = conn.prepareStatement(updateFingerprintSql);
            fingerprintPs.setString(1, student.getStud_id());
            fingerprintPs.setBytes(2, student.getFingerprint());
            fingerprintPs.setBytes(3, student.getFingerprintImage());
            fingerprintPs.executeUpdate();
            save = true;
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return save;
    }

    @Override
    public boolean update(StudentModel student) {
        boolean update = false;
        try {
            String userSql = "UPDATE user SET fname = ?, mname = ?, lname = ?, contact_num = ?, email = ?, barangay = ?, municipality = ?, sex = ?, birthdate = ?, image = ?, college = ? WHERE user_id = ?";
            PreparedStatement userPs = conn.prepareStatement(userSql);
            userPs.setString(1, student.getFname());
            userPs.setString(2, student.getMname());
            userPs.setString(3, student.getLname());
            userPs.setString(4, student.getCntctNmber());
            userPs.setString(5, student.getEmail());
            userPs.setString(6, student.getBrgy());
            userPs.setString(7, student.getMunicipal());
            userPs.setString(8, student.getSx());
            userPs.setDate(9, new java.sql.Date(student.getBday().getTime()));
            userPs.setBytes(10, student.getImage());
            userPs.setString(11, student.getCollege());
            userPs.setString(12, student.getStud_id());
            userPs.executeUpdate();

            String infoSql = "UPDATE student_info SET year = ?, section = ? WHERE user_id = ?";
            PreparedStatement infoPs = conn.prepareStatement(infoSql);
            infoPs.setString(1, student.getYear());
            infoPs.setString(2, student.getSection());
            infoPs.setString(3, student.getStud_id());
            infoPs.executeUpdate();

            if (student.getFingerprint() != null && student.getFingerprintImage() != null) {
                String checkSql = "SELECT fingerprint_template, fingerprint_image FROM identification WHERE user_id = ?";
                PreparedStatement checkPs = conn.prepareStatement(checkSql);
                checkPs.setString(1, student.getStud_id());
                rs = checkPs.executeQuery();

                if (rs.next()) {
                    byte[] oldFingerprint = rs.getBytes("fingerprint_template");
                    byte[] oldFingerprintImage = rs.getBytes("fingerprint_image");

                    String historySql = "INSERT INTO fingerprint_history (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement historyPs = conn.prepareStatement(historySql);
                    historyPs.setString(1, student.getStud_id());
                    historyPs.setBytes(2, oldFingerprint);
                    historyPs.setBytes(3, oldFingerprintImage);
                    historyPs.executeUpdate();

                    String updateFingerprintSql = "UPDATE identification SET fingerprint_template = ?, fingerprint_image = ? WHERE user_id = ?";
                    PreparedStatement updateFpPs = conn.prepareStatement(updateFingerprintSql);
                    updateFpPs.setBytes(1, student.getFingerprint());
                    updateFpPs.setBytes(2, student.getFingerprintImage());
                    updateFpPs.setString(3, student.getStud_id());
                    updateFpPs.executeUpdate();

                    System.out.println("Fingerprint and image updated, history saved.");
                } else {
                    String insertFpSql = "INSERT INTO identification (user_id, fingerprint_template, fingerprint_image) VALUES (?, ?, ?)";
                    PreparedStatement insertFpPs = conn.prepareStatement(insertFpSql);
                    insertFpPs.setString(1, student.getStud_id());
                    insertFpPs.setBytes(2, student.getFingerprint());
                    insertFpPs.setBytes(3, student.getFingerprintImage());
                    insertFpPs.executeUpdate();

                    System.out.println("Fingerprint and image inserted.");
                }
            } else {
                System.out.println("No fingerprint data provided — only user details updated.");
            }
            update = true;
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return update;
    }

    @Override
    public void delete(String stud_id) {
        try {
            String sql = "UPDATE user SET is_active = 0 WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, stud_id);
            stmt.execute();
            JOptionPane.showMessageDialog(null, "Student Deleted!");
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }
    }

    @Override
    public StudentModel studentView(String stud_id) {
        StudentModel student = new StudentModel();
        try {
            String sql = "SELECT u.image, i.fingerprint_template, i.fingerprint_image FROM user u "
                    + "LEFT JOIN identification i ON u.user_id = i.user_id WHERE u.user_id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, stud_id);
            rs = ps.executeQuery();
            if (rs.next()) {
                byte[] imageBytes = rs.getBytes("image");
                byte[] fingerprintBytes = rs.getBytes("fingerprint_template");
                byte[] fngrprntImgBytes = rs.getBytes("fingerprint_image");

                student.setFingerprintData(fingerprintBytes);
                student.setImageData(imageBytes);
                student.setFngrprntImageData(fngrprntImgBytes);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return student;
    }

}
