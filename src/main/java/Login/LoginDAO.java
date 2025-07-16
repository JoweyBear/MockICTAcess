
package Login;

import Admin.AdminModel;

public interface LoginDAO {
    AdminModel adminLogin(String user, String pass);
//    void verifyAdminLogin(byte);
}
