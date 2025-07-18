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
            List<AdminModel> admins = dao.verifyAdminLogin();
            boolean matched = false;
            AdminModel matchedAdmin = null;

            try {
                if (!scanner.initializeReader()) {
                    return;
                }
                if (!scanner.captureFingerprint()) {
                    return;
                }

                Fmd scannedFmd = scanner.getCapturedFmd();
                if (scannedFmd == null) {
                    return;
                }

                for (int i = 0; i < admins.size(); i++) {
                    boolean ok = scanner.matchFingerprint(admins.get(i).getFingerprint(), scannedFmd);
                    if (ok) {
                        matched = true;
                        matchedAdmin = admins.get(i);
                        break;
                    }
                    int progress = (int) ((i + 1) * 100f / admins.size());
                    SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
                }
            } finally {
                scanner.closeReader();
            }

            final boolean finalMatch = matched;
            final AdminModel user = matchedAdmin;
            SwingUtilities.invokeLater(() -> {
                dialog.dispose();
                if (finalMatch) {
                    JOptionPane.showMessageDialog(frameFP, "Welcome, " + user.getStFname() + "!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    new Dashboard().setVisible(true);
                    frameFP.setVisible(false);
                } else {
                    JOptionPane.showMessageDialog(frameFP, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        dialog.setVisible(true);
    }

}
