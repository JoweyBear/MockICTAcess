/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Fingerprint;

/**
 *
 * @author Administrator
 */
public class Usage {
//    verifyButton.addActionListener(e -> {
//    verifyButton.setEnabled(false); // Prevent multiple clicks
//
//    try {
//        int userIdToMatch = Integer.parseInt(userIdField.getText().trim()); // Or get from model
//        int delayTimeInMs = 4000; // Optional delay for capture
//
//        VerificationThread verificationThread = new VerificationThread(userIdToMatch, delayTimeInMs);
//        verificationThread.start();
//
//        // Optional: Re-enable the button after verification completes
//        new Thread(() -> {
//            while (verificationThread.runThisThread) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            SwingUtilities.invokeLater(() -> verifyButton.setEnabled(true));
//        }).start();
//
//    } catch (NumberFormatException ex) {
//        JOptionPane.showMessageDialog(null, "Invalid user ID. Please enter a number.");
//        verifyButton.setEnabled(true);
//    }
//});
    
//    enrollButton.addActionListener(e -> {
//    enrollButton.setEnabled(false); // Prevent multiple clicks
//
//    try {
//        int userIdToEnroll = Integer.parseInt(userIdField.getText().trim()); // Or get from model
//
//        // Make sure components are visible
//        fingerprintLabel.setVisible(true);
//        progressBar.setVisible(true);
//
//        // Link progress bar to PromptSwing
//        PromptSwing.promptProgressBar = progressBar;
//
//        // Start enrollment thread
//        EnrollmentThread enrollmentThread = new EnrollmentThread(fingerprintLabel, userIdToEnroll, progressBar);
//        enrollmentThread.start();
//
//        // Optional: Re-enable button after enrollment completes
//        new Thread(() -> {
//            while (enrollmentThread.runThisThread) {
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException ex) {
//                    ex.printStackTrace();
//                }
//            }
//
//            SwingUtilities.invokeLater(() -> enrollButton.setEnabled(true));
//        }).start();
//
//    } catch (NumberFormatException ex) {
//        JOptionPane.showMessageDialog(null, "Invalid user ID. Please enter a number.");
//        enrollButton.setEnabled(true);
//    }
//});
    
// identifyAndVerifyButton.addActionListener(e -> {
//    identifyAndVerifyButton.setEnabled(false);
//
//    int expectedUserId = Integer.parseInt(expectedUserIdField.getText().trim()); // e.g. logged-in user
//
//    // Step 1: Identify the user
//    IdentificationThread identificationThread = new IdentificationThread(fingerprintLabel);
//    identificationThread.runThisThread = true;
//
//    new Thread(() -> {
//        try {
//            identificationThread.startIdentification(); // blocking call
//
//            // Assume identificationThread sets matchingUserId internally
//            int identifiedUserId = identificationThread.getMatchedUserId(); // You’ll need to expose this
//
//            // Step 2: Verify the fingerprint matches expected user
//            VerificationThread verificationThread = new VerificationThread(expectedUserId);
//            verificationThread.runThisThread = true;
//            verificationThread.start();
//
//            // Wait for verification to complete
//            while (verificationThread.runThisThread) {
//                Thread.sleep(500);
//            }
//
//            // Step 3: Show result
//            SwingUtilities.invokeLater(() -> {
//                if (verificationThread.userIsVerified) {
//                    JOptionPane.showMessageDialog(null, "User verified successfully.");
//                } else {
//                    JOptionPane.showMessageDialog(null, "Verification failed. Fingerprint does not match expected user.");
//                }
//                identifyAndVerifyButton.setEnabled(true);
//            });
//
//        } catch (Exception ex) {
//            ex.printStackTrace();
//            SwingUtilities.invokeLater(() -> {
//                JOptionPane.showMessageDialog(null, "Error during identification/verification.");
//                identifyAndVerifyButton.setEnabled(true);
//            });
//        }
//    }).start();
//});
    
//- Add a getMatchedUserId() method to IdentificationThread that returns the matched user ID
//- Ensure IdentificationThread stores the matched ID after success

    
}
