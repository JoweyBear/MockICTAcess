package com.trialapp.trialams1;

import Main.Views.*;
import AdminDashboard.Views.Dashboard;
import Main.*;
import Utilities.Time;
import com.formdev.flatlaf.FlatLaf;
import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import nu.pattern.OpenCV;

public class TrialAMS1 {

    ImageIcon icon = null;
    URL iconURL = getClass().getResource("/Images/fingerprinticon.png");

    public static void main(String[] args) {

        FlatLaf.registerCustomDefaultsSource("Themes");
        FlatLightLaf.setup();
        OpenCV.loadLocally();

        new TrialAMS1();

//        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
//        String[] fontnames = ge.getAvailableFontFamilyNames();
//        for (String fontname : fontnames) {
//            System.out.println("Font Name" + fontname);
//        }
    }

    public TrialAMS1() {
//        Dashboard wf = new Dashboard();
        DisplayPanel dp = new DisplayPanel();
        MainFrame wf = new MainFrame(dp);
        icon = new ImageIcon(iconURL);
        Image scaledImage = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
        wf.setIconImage(scaledImage);
        MainPanel mp = new MainPanel();
        new MainController(wf, mp);
        Time t = new Time(wf);
        t.setTime();
        wf.setVisible(true);
    }
}
