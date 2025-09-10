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

        private final IdentificationThread identificationThread = new IdentificationThread();
    public final Engine engine = UareUGlobal.GetEngine();
    private final List<Fmd> fmdList = new ArrayList<>();
    private final List<FingerprintModel> enrolledFingerprints = new ArrayList<>();
    private FingerprintModel fpModel;
    private CaptureThread captureThread;

    private volatile boolean running = true;
    private final int requiredFmdToEnroll = 4;

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

    public void requestStop() {
        running = false;
    }

    public boolean isRunning() {
        
        return running;
    }

    public FingerprintModel getEnrollUser() {
        return fpModel;
    }

    @Override
    public void run() {
        try {
            Selection.setCaptureInProgress(true);
            if (Selection.readerIsConnected_noLogging()) {
                Selection.resetReader();
                startEnrollment();
            } else {
                updateProgress("Reader disconnected. Enrollment cancelled.", -1);
                PromptSwing.prompt(PromptSwing.READER_DISCONNECTED);
            }
        } catch (UareUException ex) {
            ex.printStackTrace();
        } finally {
            stopEnrollmentThread();
            Selection.setCaptureInProgress(false);
        }
    }

    private void startEnrollment() throws UareUException {
        int counter = 0;
        int attempts = 0;
        int maxAttempts = requiredFmdToEnroll * 4;

        fmdList.clear();
        enrolledFingerprints.clear();
        PromptSwing.prompt(PromptSwing.START_CAPTURE);

        while (counter < requiredFmdToEnroll && isRunning() && attempts < maxAttempts) {
            updateProgress("Capturing fingerprint " + (counter + 1) + " of " + requiredFmdToEnroll, counter);
            System.out.println("User ID to Enroll: " + userIdToEnroll);
            System.out.println("Attempt " + attempts);

            try {
                Fmd fmdToEnroll = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);
                attempts++;

                if (fmdToEnroll == null || fmdToEnroll.getData() == null || fmdToEnroll.getData().length < 100) {
                    System.out.println("Invalid FMD: null or insufficient data.");
                    continue;
                }

                fmdList.add(fmdToEnroll);
                counter++;
                PromptSwing.prompt(PromptSwing.ANOTHER_CAPTURE);

            } catch (UareUException ex) {
                attempts++;
                updateProgress("Unable to enroll. Restarting capture...", counter);
                PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
                ex.printStackTrace();
            }
        }

        if (!isRunning()) {
            updateProgress("Enrollment cancelled by user.", -1);
            return;
        }

        if (attempts >= maxAttempts) {
            updateProgress("Enrollment cancelled: too many failed attempts.", -1);
            PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
            return;
        }

        System.out.println("Total valid FMDs collected: " + fmdList.size());

        Fmd finalFmd = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);
        if (finalFmd == null || finalFmd.getData() == null || finalFmd.getData().length < 100) {
            updateProgress("Enrollment failed or template is null.", -1);
            PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
            return;
        }

        if (identificationThread.fmdIsAlreadyEnrolled(finalFmd, null)) {
            updateProgress("Fingerprint already exists in database. Enrollment cancelled.", -1);
            PromptSwing.prompt(PromptSwing.ALREADY_ENROLLED);
            return;
        }

        for (Fmd fmd : fmdList) {
            if (fmd != null && fmd.getData() != null && fmd.getData().length >= 100) {
                FingerprintModel model = new FingerprintModel();
                model.setUser_id(userIdToEnroll);
                model.setTemplate(fmd.getData());
                enrolledFingerprints.add(model);
            }
        }

        fpModel = enrolledFingerprints.isEmpty() ? null : enrolledFingerprints.get(0);
        updateProgress("Enrollment complete.", requiredFmdToEnroll);
        PromptSwing.prompt(PromptSwing.DONE_CAPTURE);
    }

    @Override
    public PreEnrollmentFmd GetFmd(Fmd.Format format) {
        while (isRunning()) {
            CaptureResult captureResult = getCaptureResultFromCaptureThread(fingerprintLabel);
            if (!isRunning()) {
                return null;
            }

            if (captureResult == null || captureResult.image == null) {
                System.out.println("CaptureResult or image is null.");
                continue;
            }

            if (captureResult.quality == Reader.CaptureQuality.CANCELED) {
                continue;
            }

            if (captureResult.quality == Reader.CaptureQuality.GOOD) {
                renderFingerprintImage(captureResult.image);

                try {
                    Fmd fmd = engine.CreateFmd(captureResult.image, format);
                    if (fmd != null && fmd.getData() != null && fmd.getData().length >= 100) {
                        Engine.PreEnrollmentFmd prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmd;
                        prefmd.view_index = 0;
                        return prefmd;
                    } else {
                        System.out.println("Invalid FMD from scan.");
                    }
                } catch (UareUException e) {
                    updateProgress("Feature extraction failed.", -1);
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private CaptureResult getCaptureResultFromCaptureThread(JLabel label) {
        captureThread = new CaptureThread("Enrollment", 3000, label);
        captureThread.start();
        try {
            captureThread.join();
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        PromptSwing.prompt(PromptSwing.CONTINUE_CAPTURE);
        CaptureThread.CaptureEvent event = captureThread.getLastCapture();
        return (event != null) ? event.captureResult : null;
    }

    private void renderFingerprintImage(Fid image) {
        Fid.Fiv[] views = image.getViews();
        if (views != null && views.length > 0) {
            BufferedImage img = Display.getFingerprintBufferedImage(views[0]);
            if (img != null && fingerprintLabel != null) {
                Image scaledImg = img.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> fingerprintLabel.setIcon(new ImageIcon(scaledImg)));
            }
        }
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

    public void stopEnrollmentThread() {
        ThreadFlags.running = false;
        running = false;
        if (captureThread != null) {
            captureThread.stopThread();
            try {
                captureThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        try {
            Selection.closeReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
        updateProgress("Enrollment stopped.", -1);
        System.out.println("Enrollment thread stopped.");
    }
}
