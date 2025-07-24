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
    private String userIdToEnroll;
    private JProgressBar progressBar;
    private CaptureThread captureThread;
    private int requiredFmdToEnroll = 2;
    public Engine engine = UareUGlobal.GetEngine();
    IdentificationThread identificationThread = new IdentificationThread();
    List<Fmd> fmdList = new ArrayList<>();
    boolean runThisThread = true;

    public EnrollmentThread(JLabel fingerprintLabel, String userIdToEnroll, JProgressBar progressBar) {
        this.fingerprintLabel = fingerprintLabel;
        this.userIdToEnroll = userIdToEnroll;
        this.progressBar = progressBar;
    }

    public void startEnrollment() throws UareUException {
        Selection.resetReader();
        int counter = 0;
        updateProgressBar(0);
        PromptSwing.prompt(PromptSwing.START_CAPTURE);

        while (counter < requiredFmdToEnroll && runThisThread) {
            System.out.println("User ID to Enroll: " + userIdToEnroll);
            System.out.println("Attempt " + counter);

            Fmd fmdToEnroll = null;

            try {
                fmdToEnroll = engine.CreateEnrollmentFmd(Fmd.Format.ISO_19794_2_2005, this);
            } catch (UareUException ex) {
                PromptSwing.prompt(PromptSwing.UNABLE_TO_ENROLL);
                stopEnrollmentThread();
                continue;
            }

            System.out.println("FMD returned");
            fmdList.add(fmdToEnroll);
            counter++;
            updateProgressBar(counter);

            if (counter < requiredFmdToEnroll) {
                PromptSwing.prompt(PromptSwing.CONTINUE_CAPTURE);
            }
        }

        for (Fmd fmd : fmdList) {
//            Fingerprint.insertFmd(userIdToEnroll, fmd);
            FingerprintModel fp = new FingerprintModel();
            fp.setUser_id(userIdToEnroll);
            byte[] fmdData = fmd.getData();
            fp.setTemplate(fmdData);
            System.out.println("Added FMD to database");
        }

        fmdList.clear();
        PromptSwing.prompt(PromptSwing.DONE_CAPTURE);
        stopEnrollmentThread();
        System.out.println("Enrollment Thread Stopped");
    }

    @Override
    public PreEnrollmentFmd GetFmd(Fmd.Format format) {
        Engine.PreEnrollmentFmd prefmd = null;

        while (prefmd == null) {
            CaptureResult captureResult = getCaptureResultFromCaptureThread(fingerprintLabel);

            if (captureResult == null) continue;
            if (Reader.CaptureQuality.CANCELED == captureResult.quality) break;

            if (Reader.CaptureQuality.GOOD == captureResult.quality) {
                try {
                    Fmd fmdToEnroll = engine.CreateFmd(captureResult.image, Fmd.Format.ISO_19794_2_2005);

                    if (!identificationThread.fmdIsAlreadyEnrolled(fmdToEnroll, fmdList)) {
                        prefmd = new Engine.PreEnrollmentFmd();
                        prefmd.fmd = fmdToEnroll;
                        prefmd.view_index = 0;
                        System.out.println("FMD Features Extracted");
                    } else {
                        PromptSwing.prompt(PromptSwing.ALREADY_ENROLLED);
                    }
                } catch (UareUException e) {
                    System.out.println("FMD Feature Extraction failed");
                }
            }
        }

        return prefmd;
    }

    private CaptureResult getCaptureResultFromCaptureThread(JLabel fingerprintLabel) {
        captureThread = new CaptureThread("EnrollCapture", 1000, fingerprintLabel);
        captureThread.start();
        try {
            captureThread.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        PromptSwing.prompt(PromptSwing.CONTINUE_CAPTURE);
        CaptureThread.CaptureEvent captureEvent = captureThread.getLastCapture();
        return captureEvent.captureResult;
    }

    public void stopEnrollmentThread() {
        if (captureThread != null) {
            runThisThread = false;
            captureThread.stopThread();
            System.out.println("Enrollment Thread Stopped");
        }
    }

    private void updateProgressBar(int value) {
        SwingUtilities.invokeLater(() -> {
            progressBar.setMaximum(requiredFmdToEnroll);
            progressBar.setValue(value);
            progressBar.setString("Scan attempt " + value + " of " + requiredFmdToEnroll);
        });
    }

    @Override
    public void run() {
        try {
            if (Selection.isReaderConnected()) {
                startEnrollment();
            } else {
                PromptSwing.prompt(PromptSwing.READER_DISCONNECTED);
                stopEnrollmentThread();
            }
        } catch (UareUException ex) {
            Logger.getLogger(EnrollmentThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}