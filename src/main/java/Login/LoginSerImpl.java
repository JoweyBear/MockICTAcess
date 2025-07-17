package Login;

import Admin.AdminModel;
import AdminDashboard.Dashboard;
import Utilities.GlobalVar;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.BorderLayout;
import java.util.List;
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
            List<AdminModel> admins = dao.verifyAdminLogin();
            boolean matched = false;
            AdminModel matchedAdmin = null;

            FingerprintCapture scanner = new FingerprintCapture();
            if (!scanner.initializeReader()) {
                System.out.println("Failed to initialize fingerprint reader.");
                return;
            }

            if (!scanner.captureFingerprint()) {
                System.out.println("Failed to capture fingerprint.");
                return;
            }

            Fmd scannedFmd = scanner.getCapturedFmd();

            if (scannedFmd != null) {
                for (int i = 0; i < admins.size(); i++) {
                    AdminModel admin = admins.get(i);
                    byte[] storedTemplate = admin.getFingerprint();

                    if (scanner.matchFingerprint(storedTemplate, scannedFmd)) {
                        matched = true;
                        matchedAdmin = admin;
                        break; // Stop after finding the first match
                    }

                    final int progress = (int) (((i + 1) / (float) admins.size()) * 100);
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }
            }

            AdminModel finalAdmin = matchedAdmin;
            boolean finalMatch = matched;

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

}
