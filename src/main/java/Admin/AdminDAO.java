package Admin;

import Utilities.FingerprintCapture;
import java.sql.ResultSet;
import javax.swing.table.DefaultTableModel;

public interface AdminDAO {

    DefaultTableModel fetchAll();
    void save(AdminModel admin);
    void update(AdminModel admin);
    void delete(String admin_id);
    AdminModel view(String admin_id);
}
