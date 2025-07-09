package Admin;

import Utilities.FingerprintCapture;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public interface AdminDAO {

    DefaultTableModel fetchAll();
    boolean save(AdminModel admin);
    boolean update(AdminModel admin);
    void delete(String admin_id);
    AdminModel view(String admin_id);
}
