package Fingerprint;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;
import com.digitalpersona.uareu.Reader.CaptureResult;
import java.awt.Image;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnrollmentThread extends Thread implements Engine.EnrollmentCallback {

    private final JLabel fingerprintLabel;
    private final JProgressBar progressBar;
    private final String userIdToEnroll;
    private CaptureThread captureThread;
    private final int requiredFmdToEnroll = 2;
    public final Engine engine = UareUGlobal.GetEngine();
    private final IdentificationThread identificationThread = new IdentificationThread();
    private final ArrayList<Fmd> fmdList = new ArrayList<>();
    private final List<FingerprintModel> enrolledFingerprints = new ArrayList<>();
    private boolean runThisThread = true;
    private FingerprintModel fpModel;

    public EnrollmentThread(JLabel fingerprintLabel, JProgressBar progressBar, String userIdToEnroll) {
        this.fingerprintLabel = fingerprintLabel;
        this.progressBar = progressBar;
        this.userIdToEnroll = userIdToEnroll;

        if (progressBar != null) {
            progressBar.setMinimum(0);
            progressBar.setMaximum(requiredFmdToEnroll);
            progressBar.setValue(0);
            progressBar.setStringPainted(true);
        }
    }

    public FingerprintModel getEnrollUser() {
        return fpModel;
    }

    public CaptureThread.CaptureEvent getLastCapture() {
        return (captureThread != null) ? captureThread.getLastCapture() : null;
    }

    public void startEnrollment() throws UareUException {
        Selection.resetReader();
        int counter = 0;
        int maxAttempts = requiredFmdToEnroll * 2;
        int attempts = 0;

        PromptSwing.prompt(PromptSwing.START_CAPTURE);

        while (counter < requiredFmdToEnroll && runThisThread && attempts < maxAttempts) {
            updateProgress("Capturing fingerprint " + (counter + 1) + " of " + requiredFmdToEnroll + " (2 times each scan)", counter);
            System.out.println("User ID to Enroll: " + userIdToEnroll);
            System.out.println("Attempt " + counter);

            Fmd fmdToEnroll = null;
            System.out.println("About to call engine.CreateEnrollmentFmd. Format: " + Fmd.Format.ANSI_378_2004 + ", Callback: " + this);
            try {
                fmdToEnroll = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);
                attempts++;

                if (fmdToEnroll == null || fmdToEnroll.getData() == null || fmdToEnroll.getData().length < 100) {
                    System.out.println("FMD created by CreateEnrollmentFmd is invalid! Data length: "
                            + (fmdToEnroll == null ? "null" : fmdToEnroll.getData() == null ? "null" : fmdToEnroll.getData().length));
                    continue;
                }
            } catch (UareUException ex) {
                attempts++;
                PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
                updateProgress("Unable to enroll. Restarting capture...", counter);
//                stopEnrollmentThread();
                continue;
            }

            fmdList.add(fmdToEnroll);
            counter++;
            PromptSwing.prompt(PromptSwing.ANOTHER_CAPTURE);
        }

        if (attempts >= maxAttempts) {
            updateProgress("Enrollment cancelled: too many failed attempts.", -1);
            PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
            stopEnrollmentThread();
            System.out.println("Enrollment Thread Stopped due to max attempts.");
            return;
        }

        Fmd finalFmd = fmdList.isEmpty() ? null : fmdList.get(0);
        if (finalFmd != null && identificationThread.fmdIsAlreadyEnrolled(finalFmd, null)) {
            PromptSwing.prompt(PromptSwing.ALREADY_ENROLLED);
            updateProgress("Fingerprint already exists in database. Enrollment cancelled.", -1);
            stopEnrollmentThread();
            return;
        }
        
        // FMD List Compatibility Check before model creation
        for (Fmd fmd : fmdList) {
            if (fmd == null || fmd.getData() == null || fmd.getData().length < 100) {
                System.out.println("Warning: Null or invalid FMD in fmdList during model creation. Data length: "
                        + (fmd == null ? "null" : fmd.getData() == null ? "null" : fmd.getData().length));
                continue;
            }
            FingerprintModel model = new FingerprintModel();
            model.setUser_id(userIdToEnroll);
            model.setTemplate(fmd.getData());
            enrolledFingerprints.add(model);
        }
        fpModel = enrolledFingerprints.isEmpty() ? null : enrolledFingerprints.get(0);

        fmdList.clear();
        updateProgress("Enrollment complete.", requiredFmdToEnroll);
        PromptSwing.prompt(PromptSwing.DONE_CAPTURE);
        stopEnrollmentThread();
        System.out.println("Enrollment Thread Stopped");
    }

    @Override
    public PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while (prefmd == null) {
            CaptureResult captureResult = getCaptureResultFromCaptureThread(fingerprintLabel);
            if (captureResult == null) {
                System.out.println("CaptureResult is null!");
                continue;
            }

            if (Reader.CaptureQuality.CANCELED == captureResult.quality) {
                continue;
            }

            if (Reader.CaptureQuality.GOOD == captureResult.quality) {
                System.out.println("Fingerprint detected.");

                // Display fingerprint image in label (UI thread safe)
                if (captureResult.image != null) {
                    Fid.Fiv[] views = captureResult.image.getViews();
                    if (views != null && views.length > 0) {
                        int targetWidth = 120;
                        int targetHeight = 120;

                        BufferedImage img = Display.getFingerprintBufferedImage(views[0]);
                        if (img != null && fingerprintLabel != null) {
                            Image scaledImg = img.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
                            SwingUtilities.invokeLater(() -> fingerprintLabel.setIcon(new ImageIcon(scaledImg)));
                        }
                    }
                } else {
                    System.out.println("CaptureResult.image is null! Skipping CreateFmd.");
                    continue;
                }
                try {
                    System.out.println("About to call engine.CreateFmd. Image: " + captureResult.image + ", Format: " + Fmd.Format.ANSI_378_2004);
                    Fmd fmdToEnroll = engine.CreateFmd(captureResult.image, Fmd.Format.ANSI_378_2004);

                    // FMD Data Compatibility Check
                    if (fmdToEnroll == null || fmdToEnroll.getData() == null || fmdToEnroll.getData().length < 100) {
                        System.out.println("FMD created from scan is invalid! Data length: "
                                + (fmdToEnroll == null ? "null" : fmdToEnroll.getData() == null ? "null" : fmdToEnroll.getData().length));
                        continue;
                    }
                    System.out.println("fmdToEnroll: " + fmdToEnroll);
                    System.out.println("fmdToEnroll data length: " + fmdToEnroll.getData().length);

                    prefmd = new Engine.PreEnrollmentFmd();
                    prefmd.fmd = fmdToEnroll;
                    prefmd.view_index = 0;
                    System.out.println("FMD Extracted");

//                    System.out.println("About to check if FMD is already enrolled...");
//                    if (!identificationThread.fmdIsAlreadyEnrolled(fmdToEnroll, fmdList)) {
//                        prefmd = new Engine.PreEnrollmentFmd();
//                        prefmd.fmd = fmdToEnroll;
//                        prefmd.view_index = 0;
//                        System.out.println("FMD Extracted");
//                    } else {
//                        PromptSwing.prompt(PromptSwing.ALREADY_ENROLLED);
//                        updateProgress("Fingerprint already exists. Please scan a different one.", -1);
//                    }
                } catch (UareUException e) {
                    updateProgress("Feature extraction failed.", -1);
                    System.out.println("Feature extraction failed");
                }
            } else {
                System.out.println("Fingerprint quality not good. Try again."); // Optional: detect poor quality input
            }
        }

        return prefmd;
    }

    private CaptureResult getCaptureResultFromCaptureThread(JLabel label) {
        captureThread = new CaptureThread(label);
        captureThread.start();
        try {
            captureThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        PromptSwing.prompt(PromptSwing.CONTINUE_CAPTURE);
        CaptureThread.CaptureEvent captureEvent = captureThread.getLastCapture();
        if (captureEvent == null) {
            System.out.println("CaptureEvent is null!");
            return null;
        }
        if (captureEvent.captureResult == null) {
            System.out.println("CaptureEvent.captureResult is null!");
        }
        return captureEvent.captureResult;
    }

    public void stopEnrollmentThread() {
        runThisThread = false;
        if (captureThread != null) {
            captureThread.stopThread();
            try {
                captureThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        updateProgress("Enrollment stopped.", -1);
    }

    @Override
    public void run() {
        try {
            System.out.println("Checking if reader is connected (no logging)...");
            if (Selection.readerIsConnected_noLogging()) {
                startEnrollment();
            } else {
                PromptSwing.prompt(PromptSwing.READER_DISCONNECTED);
                updateProgress("Reader disconnected. Enrollment cancelled.", -1);
                stopEnrollmentThread();
            }
        } catch (UareUException ex) {
            Logger.getLogger(EnrollmentThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean isRunning() {
        return ThreadFlags.running;
    }

    private void updateProgress(String message, int step) {
        if (progressBar != null) {
            SwingUtilities.invokeLater(() -> {
                if (step >= 0) {
                    progressBar.setValue(step);
                }
                progressBar.setString(message);
            });
        }
    }
}
