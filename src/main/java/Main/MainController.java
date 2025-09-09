package Main;

import Main.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

public class MainController {

    MainFrame frame;
    MainPanel panel;
    MainService service;

    public MainController(MainFrame frame, MainPanel panel) {
        this.frame = frame;
        this.panel = panel;
        this.frame.buttonListener(new ButtonEvent());
        service = new MainSerImpl(frame, panel);

        JRootPane rootPane = this.frame.getRootPane();

        InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = rootPane.getActionMap();

        inputMap.put(KeyStroke.getKeyStroke("I"), "timeIn");
        actionMap.put("timeIn", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                service.typeIDToTimeIn();
                System.out.println("I clicked");
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
