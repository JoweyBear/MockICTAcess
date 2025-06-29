package Main;

import Main.Views.MainFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainController {
    MainFrame frame;
    MainService service;
    
    public MainController(MainFrame frame){
        this.frame = frame;
        this.frame.buttonListener(new ButtonEvent());
        service = new MainSerImpl(frame);
    }
    
    class ButtonEvent implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == frame.lgn){
                service.loginButton();
                
            }
        }
        
    }
}
