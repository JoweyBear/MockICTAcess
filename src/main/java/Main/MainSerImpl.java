package Main;

import Main.Views.MainFrame;
import Login.*;

public class MainSerImpl implements MainService {
    MainFrame frame;
    MainDAO dao = new MainDAOImpl();

    public MainSerImpl(MainFrame frame){
        this.frame = frame;
    }
    @Override
    public void loginButton() {
        LoginFrame lgnfrm = new LoginFrame();
        new LoginController(lgnfrm);
        lgnfrm.setVisible(true);
        frame.setVisible(false);
    }

}
