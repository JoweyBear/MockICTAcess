
package Login;

import Admin.AdminModel;
import java.util.List;

public interface LoginDAO {
    AdminModel adminLogin(String user, String pass);
    List<AdminModel> verifyAdminLogin();
}
