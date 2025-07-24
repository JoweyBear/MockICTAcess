package Fingerprint;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Engine.Candidate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class VerificationThread extends Thread {
    private Engine engine = UareUGlobal.GetEngine();
    private CaptureThread captureThread;
    private List<FingerprintModel> fingerprintList;
    private int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    private int candidateCount = 1;
    private String userIdToMatch;
    private int delayTimeInMs = 4000;

    public boolean userIsVerified;
    public boolean isCaptureCanceled;
    public boolean runThisThread = true;
    
    FingerprintDAO dao =  new FingerprintDAOImpl();

    // Constructors
    public VerificationThread(String userIdToMatch, int delayTimeInMs) {
        this.userIdToMatch = userIdToMatch;
        this.delayTimeInMs = delayTimeInMs;
    }

    public VerificationThread(String userIdToMatch) {
        this.userIdToMatch = userIdToMatch;
    }

    public void startVerification() throws InterruptedException, UareUException {
        Selection.resetReader();
        ThreadFlags.runVerificationThread = true;
        System.out.println("Verification Thread Started");

        while (runThisThread) {
            Fmd fmdToIdentify = getFmdFromCaptureThread();
            Fmd[] databaseFmds = getFmdsFromDatabase();
            isFingerprintMatchWithUserId(fmdToIdentify, databaseFmds);
        }

        ThreadFlags.runVerificationThread = false;
        System.out.println("Verification Thread Stopped");
    }

    private Fmd getFmdFromCaptureThread() throws UareUException, InterruptedException {
        captureThread = new CaptureThread("Verification Thread", delayTimeInMs);
        captureThread.start();
        captureThread.join();

        isCaptureCanceled = captureThread.isCaptureCanceled;

        CaptureThread.CaptureEvent evt = captureThread.getLastCapture();
        if (evt == null || evt.captureResult.image == null) {
            System.out.println("Capture failed or image is null");
            return null;
        }

        return engine.CreateFmd(evt.captureResult.image, Fmd.Format.ISO_19794_2_2005);
    }

    private Fmd[] getFmdsFromDatabase() throws UareUException {
        fingerprintList = new ArrayList<>(dao.getFingerprints());
        Fmd[] fmds = new Fmd[fingerprintList.size()];

        for (int i = 0; i < fingerprintList.size(); i++) {
            if (fingerprintList.get(i).getTemplate() != null) {
                fmds[i] = UareUGlobal.GetImporter().ImportFmd(
                        fingerprintList.get(i).getTemplate(),
                        Fmd.Format.ISO_19794_2_2005,
                        Fmd.Format.ISO_19794_2_2005
                );
            }
        }

        return fmds;
    }

    private void isFingerprintMatchWithUserId(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
        if (fmdToIdentify == null) {
            userIsVerified = false;
            runThisThread = false;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Verification failed: No fingerprint captured.");
            });
            return;
        }

        Candidate[] candidateFmds = engine.Identify(fmdToIdentify, 0, databaseFmds, falsePositiveRate, candidateCount);

        if (candidateFmds.length != 0) {
            int topCandidateFmdIndex = candidateFmds[0].fmd_index;
            String matchingUserId = fingerprintList.get(topCandidateFmdIndex).getUser_id();

            if (matchingUserId == userIdToMatch) {
                userIsVerified = true;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Verification successful: Fingerprint matches user ID.");
                });
            } else {
                userIsVerified = false;
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, "Verification failed: Fingerprint does not match user ID.");
                });
            }
        } else {
            userIsVerified = false;
            SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null, "Verification failed: No matching fingerprint found.");
            });
        }

        runThisThread = false;
    }

    @Override
    public void run() {
        try {
            startVerification();
        } catch (InterruptedException | UareUException ex) {
            ex.printStackTrace();
        }
    }
}