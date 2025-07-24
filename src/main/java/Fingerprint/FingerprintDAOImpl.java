package Fingerprint;

import Connection.Ticket;
import Utilities.Encryption;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FingerprintDAOImpl implements FingerprintDAO {

    Connection conn;
    ResultSet rs;
    Encryption de = new Encryption();

    public FingerprintDAOImpl() {
        conn = Ticket.getConn();
    }

    @Override
    public FingerprintModel getUserByUserId(String userId) {
        FingerprintModel user = null;
        String sql = "SELECT * FROM user WHERE user_id = ? AND is_active = 1";
        try {
            PreparedStatement smt = conn.prepareStatement(sql);
            smt.setString(1, userId);
            rs = smt.executeQuery();

            if (rs.next()) {
                user = new FingerprintModel();
                user.setUser_id(userId);
                user.setRole(rs.getString("role"));
                user.setFname(de.decrypt(rs.getString("fname")));
                user.setMname(de.decrypt(rs.getString("mname")));
                user.setLname(de.decrypt(rs.getString("lname")));
                user.setBarangay(de.decrypt(rs.getString("barangay")));
                user.setMunicipality(de.decrypt(rs.getString("municipality")));
                user.setContact_num(rs.getString("contact_num"));
                user.setEmail(rs.getString("email"));
                user.setSex(rs.getString("sex"));
                java.sql.Date sqlBirthDate = rs.getDate("birthdate");
                Date birthDate = new Date(sqlBirthDate.getTime()); 
                user.setBirthdate((java.sql.Date) birthDate);
                user.setImage(rs.getBytes("image"));
                user.setCollege(rs.getString("college"));
            }

        } catch (SQLException ex) {
            Logger.getLogger(FingerprintDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FingerprintDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }

    @Override
    public List<FingerprintModel> getFingerprints() {
    List<FingerprintModel> fingerprints = new ArrayList<>();

    try {
        String query = "SELECT user_id, fingerprint_template, fingerprint_image FROM identification";
        PreparedStatement preparedStatement = conn.prepareStatement(query);
        rs = preparedStatement.executeQuery();

        while (rs.next()) {
            FingerprintModel fp = new FingerprintModel();
            fp.setUser_id(rs.getString("user_id"));
            fp.setTemplate(rs.getBytes("fingerprint_template"));
            fp.setTemplate_image(rs.getBytes("fingerprint_imaage"));
            fingerprints.add(fp);
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }

    return fingerprints;
}

}
