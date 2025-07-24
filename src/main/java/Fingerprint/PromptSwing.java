package Fingerprint;

import javax.swing.*;

public class PromptSwing {

    public static JProgressBar promptProgressBar;

    public static final String START_CAPTURE = "Place finger on reader to begin";
    public static final String CONTINUE_CAPTURE = "Lift and scan again with the same finger";
    public static final String DONE_CAPTURE = "Enrollment Complete";
    public static final String ALREADY_ENROLLED = "Already Enrolled, Try another finger";
    public static final String UNABLE_TO_ENROLL = "Unable to Enroll. Try again.";
    public static final String READER_DISCONNECTED = "Reader not connected.";

    public static void prompt(String message) {
        if (promptProgressBar != null) {
            SwingUtilities.invokeLater(() -> {
                promptProgressBar.setStringPainted(true);
                promptProgressBar.setString(message);
            });
        }
    }
}
