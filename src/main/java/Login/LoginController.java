package Login;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class LoginController {

    LoginFrame frame;
    LoginFrameFPrint frameFP;
    LoginService service;

    public LoginController(LoginFrame frame, LoginFrameFPrint frameFP) {
        this.frame = frame;
        this.frameFP = frameFP;
        service = new LoginSerImpl(frame, frameFP);
        this.frame.ntr.addActionListener((ActionEvent e) -> {
            service.login();
        });
        this.frameFP.forPass.addActionListener((ActionEvent e) ->{
            frameFP.setVisible(false);
            frame.setVisible(true);
        });
        this.frameFP.lgn.addActionListener((ActionEvent e) ->{
            service.authentication();
        });
        this.frame.psswrd.addKeyListener(new KeyListener() {
            public void actionPerformed(KeyEvent evt) {
                System.out.println("Handled by unknown class listener");
            }

            @Override
            public void keyTyped(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    service.login();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        });

    }

}
