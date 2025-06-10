package Login;

import Admin.AdminModel;
import AdminDashboard.Dashboard;
import Utilities.GlobalVar;
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.*;

public class LoginSerImpl implements LoginService {

    LoginDAO dao = new LoginDAOImpl();
    LoginFrame frame;

    public LoginSerImpl(LoginFrame frame) {
        this.frame = frame;
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
        // Create modal dialog
        JDialog dialog = new JDialog(frame, "Authenticating", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(350, 150);
        dialog.setLocationRelativeTo(frame);

        JLabel messageLabel = new JLabel("Authentication... Please wait...", SwingConstants.CENTER);
        dialog.add(messageLabel, BorderLayout.NORTH);

        JProgressBar progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true); // shows %
        dialog.add(progressBar, BorderLayout.CENTER);

        // Submit authentication task
        executor.submit(() -> {
            boolean result = false;

            for (int i = 0; i <= 100; i++) {
                int progress = i;

                // Update progress on Swing thread
                SwingUtilities.invokeLater(() -> progressBar.setValue(progress));

                try {
                    Thread.sleep(30); // simulate time taken
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // Simulated fingerprint authentication result
            result = Math.random() > 0.3;

            // Show result after completion
            boolean finalResult = result;
            SwingUtilities.invokeLater(() -> {
                dialog.dispose(); // close loading dialog

                if (finalResult) {
                    JOptionPane.showMessageDialog(frame, "Authentication successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    // TODO: Redirect to next screen
                } else {
                    JOptionPane.showMessageDialog(frame, "Authentication failed.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            });
        });

        dialog.setVisible(true); // blocks until disposed
    }

    public void shutdownExecutor() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.shutdown();
        try {
            if (!executor.awaitTermination(3, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

    }

}
