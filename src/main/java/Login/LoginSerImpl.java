package Login;

import Admin.AdminModel;
import AdminDashboard.DashboardController;
import AdminDashboard.Views.*;
import Fingerprint.FingerprintModel;
import Fingerprint.IdentificationThread;
import Fingerprint.PromptSwing;
import Fingerprint.Selection;
import Utilities.GlobalVar;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class LoginSerImpl implements LoginService {

    LoginDAO dao = new LoginDAOImpl();
    LoginFrame frame;
    LoginFrameFPrint frameFP;

    public LoginSerImpl(LoginFrame frame, LoginFrameFPrint frameFP) {
        this.frame = frame;
        this.frameFP = frameFP;
    }

    @Override
    public void login() {
        System.out.println("I was here");
        String user = frame.srnm.getText();
        String pass = frame.psswrd.getText();
        AdminModel admin = dao.adminLogin(user, pass);
        GlobalVar.loggedInAdmin = admin;
        if (admin != null) {
            System.out.println("Log in successful");
            DashboardPanel panel = new DashboardPanel();
            Dashboard dashboard = new Dashboard(panel);
            new DashboardController(dashboard, panel);
            dashboard.setVisible(true);
            frame.setVisible(false);
        }

    }

    @Override
    public void authentication() {

    }

    @Override
    public void identifyAdmin() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        JDialog dialog = new JDialog(frameFP, "Authenticating", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(frameFP);

        JLabel messageLabel = new JLabel("Initializing...", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Preparing...");
        dialog.add(progressBar, BorderLayout.CENTER);

        PromptSwing.promptProgressBar = progressBar;

        ReaderCollection readers;
        try {
            readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();

            if (readers.size() == 0) {
                JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
                frameFP.lgn.setEnabled(true);
                return;
            }
            if (readers.get(0) == null) {
                JOptionPane.showMessageDialog(null, "Fingerprint reader object is null.");
                frameFP.lgn.setEnabled(true);
                return;
            }

            Selection.reader = readers.get(0);
        } catch (UareUException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing fingerprint reader: " + e.getMessage());
            frameFP.lgn.setEnabled(true);
            return;
        }

        executor.submit(() -> {
            IdentificationThread idThread = new IdentificationThread(progressBar, messageLabel);
            idThread.start();

            try {
                idThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            FingerprintModel matchedAdmin = idThread.getIdentifiedUser();

            SwingUtilities.invokeLater(() -> {

                if (matchedAdmin != null) {
                    System.out.println("Role: " + matchedAdmin.getRole());

                    if (matchedAdmin.getRole().equals("admin")) {
                        AdminModel admin = dao.AdminInfo(matchedAdmin);
                        GlobalVar.loggedInAdmin = admin;
                        if (admin != null) {
                            progressBar.setString("Loading admin dashboard...");
                            messageLabel.setText("Access granted. Redirecting...");

                            String name = matchedAdmin.getFname() + " " + matchedAdmin.getLname();
                            JOptionPane.showMessageDialog(null, "Welcome " + name + "!", "Authenticated", JOptionPane.INFORMATION_MESSAGE);
                            DashboardPanel panel = new DashboardPanel();
                            Dashboard dashboard = new Dashboard(panel);
                            new DashboardController(dashboard, panel);
                            dashboard.setVisible(true);
                            frameFP.setVisible(false);
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Access denied. You are not an admin.");
                    }
                } else {
                    progressBar.setString("Authentication failed.");
                    messageLabel.setText("Unknown fingerprint.");
                    JOptionPane.showMessageDialog(null, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }

                dialog.dispose();
                executor.shutdown();
            });

        });

        dialog.setVisible(true);

    }

    @Override
    public void checkThreadsActivity() {
        if (Selection.reader != null && Selection.readerIsConnected()) {
            System.out.println("Reader is already open and connected");

            if (Selection.isAnotherThreadCapturing()) {
                identifyAdmin();
            } else {
                System.out.println("Another capture uis in progress -- cancelling..");
                Selection.requestCurrentCaptureCancel();
                Selection.waitForCaptureToFinish();
               identifyAdmin();
            }
        } else {
            System.out.println("Reader is not open..Opeining..");
            Selection.resetReader();
            identifyAdmin();
        }
    }
    }
