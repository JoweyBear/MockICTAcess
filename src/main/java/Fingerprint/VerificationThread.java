package Fingerprint;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.Candidate;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.util.List;

import javax.swing.*;

public class VerificationThread extends Thread {

    private Engine engine = UareUGlobal.GetEngine();
    private CaptureThread captureThread;
    private List<FingerprintModel> fingerprintList;
    private int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    private int candidateCount = 1;
    private String userIdToMatch;
    private int delayTimeInMs;
    public boolean userIsVerified;
    public boolean isCaptureCanceled;
    boolean runThisThread = true;
    FingerprintDAO dao = new FingerprintDAOImpl();

    // UI Feedback
    private JProgressBar progressBar;
    private JLabel statusLabel;

    // Constructor
    public VerificationThread(String userIdToMatch, int delayTimeInMs, JProgressBar progressBar, JLabel statusLabel) {
        this.userIdToMatch = userIdToMatch;
        this.delayTimeInMs = delayTimeInMs;
        this.progressBar = progressBar;
        this.statusLabel = statusLabel;
    }

    public VerificationThread(String userIdToMatch, JProgressBar progressBar, JLabel statusLabel) {
        this(userIdToMatch, 2000, progressBar, statusLabel);
    }

    public void startVerification() throws InterruptedException, UareUException {
        Selection.resetReader();
        ThreadFlags.runVerificationThread = true;
        updateProgress(20, "Initializing...");

        while (runThisThread) {
            updateProgress(40, "Capturing fingerprint...");
            Fmd fmdToIdentify = getFmdFromCaptureThread();

            if (isCaptureCanceled) {
                updateProgress(0, "Capture cancelled.");
                break;
            }

            updateProgress(60, "Retrieving fingerprint data...");
            Fmd[] databaseFmds = getFmdsFromDatabase();

            updateProgress(80, "Verifying fingerprint...");
            isFingerprintMatchWithUserId(fmdToIdentify, databaseFmds);
        }

        updateProgress(100, userIsVerified ? "Verification successful!" : "Verification failed.");
        ThreadFlags.runVerificationThread = false;
    }

    private Fmd getFmdFromCaptureThread() throws UareUException, InterruptedException {
        captureThread = new CaptureThread("Verification Thread", delayTimeInMs, statusLabel);
        captureThread.start();
        captureThread.join();

//        isCaptureCanceled = captureThread.isCaptureCanceled;
        CaptureThread.CaptureEvent evt = captureThread.getLastCapture();

        if (evt == null || evt.captureResult.image == null) {
            return null;
        }

        return engine.CreateFmd(evt.captureResult.image, Fmd.Format.ANSI_378_2004);
    }

    private Fmd[] getFmdsFromDatabase() throws UareUException {
        fingerprintList = dao.getFingerprints();
        Fmd[] fmds = new Fmd[fingerprintList.size()];

        for (int i = 0; i < fingerprintList.size(); i++) {
            if (fingerprintList.get(i).getTemplate() != null) {
                fmds[i] = UareUGlobal.GetImporter().ImportFmd(
                        fingerprintList.get(i).getTemplate(),
                        Fmd.Format.ANSI_378_2004,
                        Fmd.Format.ANSI_378_2004
                );
            }
        }

        return fmds;
    }

    private void isFingerprintMatchWithUserId(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
        if (fmdToIdentify == null) {
            userIsVerified = false;
            runThisThread = false;
            return;
        }

        Candidate[] candidateFmds = engine.Identify(fmdToIdentify, 0, databaseFmds, falsePositiveRate, candidateCount);
        if (candidateFmds.length > 0) {
            int matchIndex = candidateFmds[0].fmd_index;
            String matchUserId = fingerprintList.get(matchIndex).getUser_id();

            if (matchUserId == userIdToMatch) {
                userIsVerified = true;
            } else {
                userIsVerified = false;
            }
        } else {
            userIsVerified = false;
        }

        runThisThread = false;
    }

    private void updateProgress(int value, String message) {
        if (progressBar != null) {
            progressBar.setValue(value);
            progressBar.setString(message);
        }
        if (statusLabel != null) {
            statusLabel.setText(message);
        }
    }

    @Override
    public void run() {
        try {
            startVerification();
        } catch (InterruptedException | UareUException e) {
            e.printStackTrace();
            updateProgress(0, "Error: " + e.getMessage());
        }
    }
}
