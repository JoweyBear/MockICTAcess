package Student;

import Connection.Ticket;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.table.DefaultTableModel;

public class StudentDAOImpl implements StudentDAO{
    Connection conn;
    ResultSet rs;
    public StudentDAOImpl(){
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
            Logger.getLogger(StudentDAOImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Return empty default if something fails
        Vector<String> columns = new Vector<>(List.of("ID", "First Name", "Middle Name", "Last Name", "Email", "Username", "Status"));
        return new DefaultTableModel(new Vector<>(), columns);
    }

    @Override
    public void save(StudentModel student) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void update(StudentModel student) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete(String stud_id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    
}
