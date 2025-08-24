package com.trialapp.trialams1;

import Main.Views.*;
import AdminDashboard.Dashboard;
import Main.*;
import Utilities.Time;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import javax.swing.UIManager;

public class TrialAMS1 {

    public static void main(String[] args) {
        FlatLaf.registerCustomDefaultsSource("Themes");
        FlatLightLaf.setup();
//        Dashboard wf = new Dashboard();
        MainFrame wf = new MainFrame();
        MainPanel mp =  new MainPanel();
        new MainController(wf, mp);
        Time t = new Time(wf);
        t.setTime();
        wf.setVisible(true);

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        String[] fontnames = ge.getAvailableFontFamilyNames();
//        for (String fontname : fontnames) {
//            System.out.println("Font Name" + fontname);
//        }
    }
}
