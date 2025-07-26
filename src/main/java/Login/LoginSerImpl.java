package Login;

import Admin.AdminModel;
import AdminDashboard.Dashboard;
import Fingerprint.FingerprintModel;
import Fingerprint.IdentificationThread;
import Utilities.GlobalVar;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
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
import utilities.FingerprintCapture;

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
        String user = frame.srnm.getText();
        String pass = frame.psswrd.getText();
        AdminModel admin = dao.adminLogin(user, pass);
        GlobalVar.loggedInAdmin = admin;
        if (admin != null) {
            Dashboard dashboard = new Dashboard();
            dashboard.setVisible(true);
            frame.setVisible(false);
        }

    }

    @Override
    public void authentication() {
        System.out.println("Scanning started...");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        JDialog dialog = new JDialog(frameFP, "Authenticating", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(frameFP);

        JLabel messageLabel = new JLabel("Scanning... Please wait...", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        dialog.add(progressBar, BorderLayout.CENTER);

        executor.submit(() -> {
            FingerprintCapture scanner = new FingerprintCapture();
            boolean matched = false;
            AdminModel matchedAdmin = null;

            List<AdminModel> admins = dao.verifyAdminLogin();
            List<Fmd> enrolledFmdList = new ArrayList<>();

            // Map index to AdminModel
            Map<Integer, AdminModel> adminMap = new HashMap<>();

            try {
                if (!scanner.initializeReader()) {
                    System.out.println("Failed to initialize fingerprint reader.");
                    return;
                }

                // Load all enrolled FMDs
                for (int i = 0; i < admins.size(); i++) {
                    AdminModel admin = admins.get(i);
                    byte[] storedBytes = admin.getFingerprint();
                    if (storedBytes != null) {
                        try {
                            Fmd fmd = UareUGlobal.GetImporter().ImportFmd(
                                    storedBytes,
                                    Fmd.Format.DP_PRE_REG_FEATURES,
                                    Fmd.Format.DP_PRE_REG_FEATURES
                            );
                            enrolledFmdList.add(fmd);
                            adminMap.put(i, admin);
                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Failed to import FMD for admin: " + admin.getStFname());
                        }
                    }
                }

                if (!scanner.captureFingerprint()) {
                    System.out.println("Failed to capture fingerprint.");
                    return;
                }

                Fmd scannedFmd = scanner.getCapturedFmd();
                if (scannedFmd == null) {
                    System.out.println("Captured FMD is null.");
                    return;
                }

                // Perform identification
                Fmd[] fmdArray = enrolledFmdList.toArray(new Fmd[0]);
                Engine engine = UareUGlobal.GetEngine();
                int falsematchRate = Engine.PROBABILITY_ONE / 100000;
                Engine.Candidate[] matches = engine.Identify(scannedFmd, 0, fmdArray, falsematchRate, 1);
                System.out.println("Matching against stored fingerprint...");
                System.out.println("Stored template size: " + fmdArray.length);
                System.out.println("Candidates found: " + matches.length);
                if (matches.length > 0) {
                    int matchedIndex = matches[0].fmd_index;
                    matchedAdmin = adminMap.get(matchedIndex);
                    matched = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                scanner.closeReader();
            }

            boolean finalMatch = matched;
            AdminModel finalAdmin = matchedAdmin;

            SwingUtilities.invokeLater(() -> {
                dialog.dispose();
                if (finalMatch && finalAdmin != null) {
                    String name = finalAdmin.getStFname() + " " + finalAdmin.getStLname();
                    JOptionPane.showMessageDialog(null, "Welcome " + name + "!", "Authenticated", JOptionPane.INFORMATION_MESSAGE);
                    Dashboard dashboard = new Dashboard();
                    dashboard.setVisible(true);
                    frameFP.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            executor.shutdown();
        });

        dialog.setVisible(true);
    }

    @Override
    public void identifyAdmin() {
        ExecutorService executor = Executors.newSingleThreadExecutor();

        JDialog dialog = new JDialog(frameFP, "Authenticating", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(frameFP);

        JLabel messageLabel = new JLabel("Scanning... Please wait...", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setStringPainted(true);
        progressBar.setString("Identifying...");
        dialog.add(progressBar, BorderLayout.CENTER);

        executor.submit(() -> {
            IdentificationThread identificationThread = new IdentificationThread();
            identificationThread.start();

            try {
                identificationThread.join(); // wait for thread to finish
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }

            FingerprintModel matchedAdmin = identificationThread.getIdentifiedUser();

            SwingUtilities.invokeLater(() -> {
                dialog.dispose();

                if (matchedAdmin != null) {
                    String name = matchedAdmin.getFname() + " " + matchedAdmin.getLname();
                    JOptionPane.showMessageDialog(null, "Welcome " + name + "!", "Authenticated", JOptionPane.INFORMATION_MESSAGE);
                    AdminModel admin = dao.AdminInfo(matchedAdmin);
                    GlobalVar.loggedInAdmin = admin;
                    if (admin != null) {
                        Dashboard dashboard = new Dashboard();
                        dashboard.setVisible(true);
                        frameFP.setVisible(false);
                    }

                } else {
                    JOptionPane.showMessageDialog(null, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });

            executor.shutdown();
        });

        dialog.setVisible(true);

    }

}
