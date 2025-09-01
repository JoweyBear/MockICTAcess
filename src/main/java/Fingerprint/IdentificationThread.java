package Fingerprint;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.Candidate;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IdentificationThread extends Thread {

    private Runnable onFingerprintScan;
    private JLabel fingerprintLabel;
    private JProgressBar progressBar;
    private Engine engine = UareUGlobal.GetEngine();
    public CaptureThread captureThread;
    List<FingerprintModel> fingerprintList;
    int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    int candidateCount = 1;
    int delayTimeInMs = 4000;

    FingerprintDAO dao = new FingerprintDAOImpl();
    private FingerprintModel identifiedUser;

    private boolean headlessMode = false;
    public boolean runThisThread = true;

    public IdentificationThread() {
        headlessMode = true;
    }

    public IdentificationThread(JProgressBar progressBar, JLabel fingerprintLabel) {
        this.progressBar = progressBar;
        this.fingerprintLabel = fingerprintLabel;
    }

    public IdentificationThread(JProgressBar progressBar, JLabel fingerprintLabel, Runnable onFingerprintScan) {
        this.progressBar = progressBar;
        this.fingerprintLabel = fingerprintLabel;
        this.onFingerprintScan = onFingerprintScan;
    }

    public void startIdentification() throws InterruptedException, UareUException {
        Selection.resetReader();
        System.out.println("Identification Thread Started");

        while (runThisThread) {
            Fmd fmdToIdentify = getFmdFromCaptureThread();
            if (!isValidFmd(fmdToIdentify, "startIdentification: fmdToIdentify")) {
                System.out.println("startIdentification: fmdToIdentify is invalid, skipping iteration.");
                continue;
            }
            Fmd[] databaseFmds = getFmdsFromDatabase();

            if (!isValidFmdArray(databaseFmds, "startIdentification: databaseFmds")) {
                System.out.println("startIdentification: databaseFmds is invalid or empty, skipping iteration.");
                continue;
            }
            compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
        }

        System.out.println("Identification Thread Stopped");
    }

    private Fmd getFmdFromCaptureThread() throws UareUException, InterruptedException {
        if (onFingerprintScan != null) {
            onFingerprintScan.run();
        }

        captureThread = new CaptureThread("Identification Thread", delayTimeInMs, fingerprintLabel);
        captureThread.start();
        captureThread.join();

        CaptureThread.CaptureEvent evt = captureThread.getLastCapture();

        if (evt == null) {
            System.out.println("getFmdFromCaptureThread: CaptureEvent is null!");
            return null;
        }
        if (evt.captureResult == null) {
            System.out.println("getFmdFromCaptureThread: CaptureEvent.captureResult is null!");
            return null;
        }
        if (evt.captureResult.image == null) {
            System.out.println("getFmdFromCaptureThread: evt.captureResult.image is null!");
            return null;
        }

        Fmd fmd = null;
        try {
            fmd = engine.CreateFmd(evt.captureResult.image, Fmd.Format.ANSI_378_2004);
            if (!isValidFmd(fmd, "getFmdFromCaptureThread: engine.CreateFmd")) {
                return null;
            }
            System.out.println("getFmdFromCaptureThread: FMD created. Data length: " + fmd.getData().length);
        } catch (UareUException e) {
            System.out.println("getFmdFromCaptureThread: Exception during CreateFmd.");
            e.printStackTrace();
        }
        return fmd;
    }

    private Fmd[] getFmdsFromDatabase() throws UareUException {
        fingerprintList = dao.getFingerprints();
        if (fingerprintList == null || fingerprintList.isEmpty()) {
            System.out.println("getFmdsFromDatabase: fingerprintList is null or empty!");
            return new Fmd[0];
        }
        Fmd[] Fmds = new Fmd[fingerprintList.size()];

        int validCount = 0;
        for (int i = 0; i < fingerprintList.size(); i++) {
            byte[] fmdBytes = fingerprintList.get(i).getTemplate();
            if (fmdBytes == null || fmdBytes.length < 100) {
                System.out.println("getFmdsFromDatabase: fingerprintList.get(" + i + ").getTemplate() is null/invalid! Length: "
                        + (fmdBytes == null ? "null" : fmdBytes.length));
                continue;
            }
            Fmd importedFmd = null;
            try {
                importedFmd = UareUGlobal.GetImporter().ImportFmd(
                        fmdBytes,
                        Fmd.Format.ANSI_378_2004,
                        Fmd.Format.ANSI_378_2004
                );
                if (!isValidFmd(importedFmd, "getFmdsFromDatabase: ImportFmd[" + i + "]")) {
                    continue;
                }
                Fmds[validCount++] = importedFmd;
            } catch (UareUException e) {
                System.out.println("getFmdsFromDatabase: Exception during ImportFmd at index " + i + ".");
                e.printStackTrace();
            }
        }
        if (validCount == 0) {
            System.out.println("getFmdsFromDatabase: No valid FMDs found.");
            return new Fmd[0];
        }
        // Return only the valid portion of the array
        Fmd[] validFmds = new Fmd[validCount];
        System.arraycopy(Fmds, 0, validFmds, 0, validCount);
        return validFmds;
    }

    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
        if (!isValidFmd(fmdToIdentify, "compareFmdToDatabaseFmds: fmdToIdentify")) {
            return false;
        }
        if (!isValidFmdArray(databaseFmds, "compareFmdToDatabaseFmds: databaseFmds")) {
            return false;
        }

        Candidate[] candidateFmds = null;
        try {
            candidateFmds = engine.Identify(
                    fmdToIdentify, 0, databaseFmds, falsePositiveRate, candidateCount);
        } catch (UareUException e) {
            System.out.println("compareFmdToDatabaseFmds: Exception during engine.Identify.");
            e.printStackTrace();
            return false;
        }

        if (candidateFmds == null) {
            System.out.println("compareFmdToDatabaseFmds: candidateFmds is null!");
            return false;
        }
        if (candidateFmds.length != 0) {
            int topCandidateFmdIndex = candidateFmds[0].fmd_index;
            if (fingerprintList == null || fingerprintList.size() <= topCandidateFmdIndex) {
                System.out.println("compareFmdToDatabaseFmds: fingerprintList is null or index out of bounds!");
                return false;
            }
            String matchingUserId = fingerprintList.get(topCandidateFmdIndex).getUser_id();
            if (!headlessMode) {
                userIdentificationSuccess(matchingUserId);
            }

//            runThisThread = false;
            return true;
        } else {
            if (!headlessMode) {
                userIdentificationFailed();
            }
            System.out.println("No candidate/s found");
            return false;
        }
    }

    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds, ArrayList<Fmd> fmdList) throws UareUException {
        if (!isValidFmd(fmdToIdentify, "compareFmdToDatabaseFmds (with fmdList): fmdToIdentify")) {
            return false;
        }
        if (!isValidFmdArray(databaseFmds, "compareFmdToDatabaseFmds (with fmdList): databaseFmds")) {
            return false;
        }
        if (!isValidFmdList(fmdList, "compareFmdToDatabaseFmds (with fmdList): fmdList")) {
            return false;
        }

        Fmd[] combinedFmds = new Fmd[databaseFmds.length + fmdList.size()];
        System.arraycopy(databaseFmds, 0, combinedFmds, 0, databaseFmds.length);

        for (int i = 0; i < fmdList.size(); i++) {
            combinedFmds[databaseFmds.length + i] = fmdList.get(i);
        }

        if (!isValidFmdArray(combinedFmds, "compareFmdToDatabaseFmds (with fmdList): combinedFmds")) {
            return false;
        }

        Candidate[] candidateFmds = null;
        try {
            candidateFmds = engine.Identify(fmdToIdentify, 0, combinedFmds, falsePositiveRate, candidateCount);
        } catch (UareUException e) {
            System.out.println("compareFmdToDatabaseFmds (with fmdList): Exception during engine.Identify.");
            e.printStackTrace();
            return false;
        }

        if (candidateFmds == null) {
            System.out.println("compareFmdToDatabaseFmds (with fmdList): candidateFmds is null!");
            return false;
        }
        if (candidateFmds.length != 0) {
            System.out.println("Candidate found");
            return true;
        } else {
            System.out.println("No candidate/s found");
            return false;
        }
    }

    private FingerprintModel userIdentificationSuccess(String userId) {
        FingerprintModel user = dao.getUserByUserId(userId);
        this.identifiedUser = user;

        runThisThread = false;

        SwingUtilities.invokeLater(() -> {
            fingerprintLabel.setIcon(null);
            System.out.println("Identification Success: " + (user != null ? user.getFname() : "Unknown User"));
            if (progressBar != null) {
                new Thread(() -> {
                    try {
                        SwingUtilities.invokeLater(() -> progressBar.setString("Fingerprint matched!"));
                        Thread.sleep(1000);

                        SwingUtilities.invokeLater(() -> progressBar.setString("Access granted. Preparing dashboard..."));
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                }).start();

            }
        });

        try {
            if (!ThreadFlags.runVerificationThread) {
                Thread.sleep(delayTimeInMs);
            } else {
                while (ThreadFlags.runVerificationThread) {
                    System.out.println("Waiting for verification thread to end capture");
                    Thread.sleep(1000);
                }
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
        return user;
    }

    public FingerprintModel getIdentifiedUser() {
        return identifiedUser;
    }

    private void userIdentificationFailed() {
        this.identifiedUser = null;
        runThisThread = false;

        SwingUtilities.invokeLater(() -> {
            if (progressBar != null) {
                progressBar.setString("Unknown Fingerprint");
            }
            JOptionPane.showMessageDialog(null, "Fingerprint not recognized.", "Authentication Failed", JOptionPane.WARNING_MESSAGE);
        });

        System.out.println("Identification failed: Unknown fingerprint.");
    }

    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        if (!isValidFmd(fmdToIdentify, "fmdIsAlreadyEnrolled: fmdToIdentify")) {
            return false;
        }
        if (!isValidFmdArray(databaseFmds, "fmdIsAlreadyEnrolled: databaseFmds")) {
            return false;
        }
        return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
    }

    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify, ArrayList<Fmd> fmdList) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        if (!isValidFmd(fmdToIdentify, "fmdIsAlreadyEnrolled (with fmdList): fmdToIdentify")) {
            return false;
        }
        if (!isValidFmdArray(databaseFmds, "fmdIsAlreadyEnrolled (with fmdList): databaseFmds")) {
            return false;
        }
        if (!isValidFmdList(fmdList, "fmdIsAlreadyEnrolled (with fmdList): fmdList")) {
            return false;
        }
        return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds, fmdList);
    }

    public void stopThread() {
        runThisThread = false;
        if (captureThread != null) {
            captureThread.stopThread();
        }
    }

    @Override
    public void run() {
        try {
            startIdentification();
        } catch (InterruptedException | UareUException ex) {
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // ----- Utility validation methods -----
    private boolean isValidFmd(Fmd fmd, String context) {
        if (fmd == null) {
            System.out.println(context + ": FMD is null!");
            return false;
        }
        if (fmd.getData() == null) {
            System.out.println(context + ": FMD.getData() is null!");
            return false;
        }
        if (fmd.getData().length < 100) {
            System.out.println(context + ": FMD.getData() too short! Length: " + fmd.getData().length);
            return false;
        }
        return true;
    }

    private boolean isValidFmdArray(Fmd[] fmds, String context) {
        if (fmds == null) {
            System.out.println(context + ": Fmd array is null!");
            return false;
        }
        if (fmds.length == 0) {
            System.out.println(context + ": Fmd array is empty!");
            return false;
        }
        for (int i = 0; i < fmds.length; i++) {
            if (!isValidFmd(fmds[i], context + " [" + i + "]")) {
                return false;
            }
        }
        return true;
    }

    private boolean isValidFmdList(ArrayList<Fmd> fmdList, String context) {
        if (fmdList == null) {
            System.out.println(context + ": Fmd list is null!");
            return false;
        }
        if (fmdList.isEmpty()) {
            System.out.println(context + ": Fmd list is empty!");
            return false;
        }
        for (int i = 0; i < fmdList.size(); i++) {
            if (!isValidFmd(fmdList.get(i), context + " [" + i + "]")) {
                return false;
            }
        }
        return true;
    }
}
