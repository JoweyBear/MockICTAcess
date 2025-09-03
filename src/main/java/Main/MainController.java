package Main;

import Main.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.AbstractAction;
import javax.swing.Action;

public class MainController {

    MainFrame frame;
    MainPanel panel;
    MainService service;

    public MainController(MainFrame frame, MainPanel panel) {
        this.frame = frame;
        this.panel = panel;
        this.frame.buttonListener(new ButtonEvent());
        service = new MainSerImpl(frame, panel);
        

        this.frame.addKeyListener(new KeyListener() {
            public void actionPerformed(KeyEvent evt) {
                System.out.println("Handled by unknown class listener");
            }

            @Override
            public void keyTyped(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    service.typeIDToTimeIn();
                    System.out.println("O clicked");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
//                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

        });
    }

    class ButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == frame.lgn) {
                service.loginButton();

            }
        }
    }
}
