package Fingerprint;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Engine.PreEnrollmentFmd;
import com.digitalpersona.uareu.Reader.CaptureResult;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnrollmentThread extends Thread implements Engine.EnrollmentCallback {

    private JLabel fingerprintLabel;
    private JProgressBar progressBar;
    private String userIdToEnroll;
    private CaptureThread captureThread;
    private int requiredFmdToEnroll = 2;
    public Engine engine = UareUGlobal.GetEngine();
    private IdentificationThread identificationThread = new IdentificationThread();
    private ArrayList<Fmd> fmdList = new ArrayList<>();
    List<FingerprintModel> enrolledFingerprints = new ArrayList<>();
    boolean runThisThread = true;
    FingerprintModel fpModel;

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

    public void startEnrollment() throws UareUException {
        Selection.closeAndOpenReader();
        int counter = 0;
        PromptSwing.prompt(PromptSwing.START_CAPTURE);

        while (counter < requiredFmdToEnroll && runThisThread) {
            updateProgress("Capturing fingerprint " + (counter + 1) + " of " + requiredFmdToEnroll, counter);
            System.out.println("User ID to Enroll: " + userIdToEnroll);
            System.out.println("Attempt " + counter);

            Fmd fmdToEnroll = null;
            System.out.println("About to call engine.CreateEnrollmentFmd. Format: " + Fmd.Format.ANSI_378_2004 + ", Callback: " + this);
            try {
                fmdToEnroll = engine.CreateEnrollmentFmd(Fmd.Format.ANSI_378_2004, this);

                // FMD Data Compatibility Check
                if (fmdToEnroll == null || fmdToEnroll.getData() == null || fmdToEnroll.getData().length < 100) {
                    System.out.println("FMD created by CreateEnrollmentFmd is invalid! Data length: "
                            + (fmdToEnroll == null ? "null" : fmdToEnroll.getData() == null ? "null" : fmdToEnroll.getData().length));
                    continue;
                }
            } catch (UareUException ex) {
                PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
                updateProgress("Unable to enroll. Restarting capture...", counter);
                stopEnrollmentThread();
                continue;
            }

            fmdList.add(fmdToEnroll);
            counter++;
            PromptSwing.prompt(PromptSwing.ANOTHER_CAPTURE);
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
        if (!enrolledFingerprints.isEmpty()) {
            fpModel = enrolledFingerprints.get(0);
        } else {
            System.out.println("No enrolled fingerprints found after enrollment!");
            fpModel = null;
        }

        fmdList.clear();
        updateProgress("Enrollment complete.", requiredFmdToEnroll);
        PromptSwing.prompt(PromptSwing.DONE_CAPTURE);
        stopEnrollmentThread();
        System.out.println("Enrollment Thread Stopped");
    }

    @Override
    public PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while (null == prefmd) {
            CaptureResult captureResult = getCaptureResultFromCaptureThread(fingerprintLabel);
            if (captureResult == null) {
                System.out.println("CaptureResult is null!");
                continue;
            }

            if (Reader.CaptureQuality.CANCELED == captureResult.quality) {
                break;
            }

            if (Reader.CaptureQuality.GOOD == captureResult.quality) {
                System.out.println("Fingerprint detected.");

                if (captureResult.image == null) {
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

                    // FMD List Compatibility Check before enrollment check
                    for (int i = 0; i < fmdList.size(); i++) {
                        Fmd fmdCheck = fmdList.get(i);
                        if (fmdCheck == null || fmdCheck.getData() == null || fmdCheck.getData().length < 100) {
                            System.out.println("fmdList[" + i + "] is invalid! Data length: "
                                    + (fmdCheck == null ? "null" : fmdCheck.getData() == null ? "null" : fmdCheck.getData().length));
                        }
                    }

                    System.out.println("About to check if FMD is already enrolled...");
                    if (!identificationThread.fmdIsAlreadyEnrolled(fmdToEnroll, fmdList)) {
                        prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmdToEnroll;
                        prefmd.view_index = 0;
                        System.out.println("FMD Extracted");
                    } else {
                        PromptSwing.prompt(PromptSwing.ALREADY_ENROLLED);
                        updateProgress("Fingerprint already exists. Please scan a different one.", -1);
                    }
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
        captureThread = new CaptureThread(label); // Second param is null because we're not using progress bar here
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
