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
        LoginFrameFPrint lgnfrmFP = new LoginFrameFPrint();
        new LoginController(lgnfrm, lgnfrmFP);
//        lgnfrm.setVisible(true);
        lgnfrmFP.setVisible(true);        
        frame.setVisible(false);
    }

}
