package Fingerprint;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Engine.Candidate;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IdentificationThread extends Thread {

    private Runnable onFingerprintScan;
    private JLabel fingerprintLabel;
    private Engine engine = UareUGlobal.GetEngine();
    public CaptureThread captureThread;
    private List<FingerprintModel> fingerprintList;
    private int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    private int candidateCount = 1;
    private int delayTimeInMs = 4000;
    private boolean headlessMode = false;
    public boolean runThisThread = true;
    FingerprintDAO dao = new FingerprintDAOImpl();
    private FingerprintModel identifiedUser;

    public IdentificationThread(JLabel fingerprintLabel) {
        this.fingerprintLabel = fingerprintLabel;
    }

    public IdentificationThread(JLabel fingerprintLabel, Runnable onFingerprintScan) {
        this.fingerprintLabel = fingerprintLabel;
        this.onFingerprintScan = onFingerprintScan;
    }

    public IdentificationThread() {
        headlessMode = true;
    }

    public void startIdentification() throws InterruptedException, UareUException {
        Selection.resetReader();
        System.out.println("Identification Thread Started");

        while (runThisThread) {
            Fmd fmdToIdentify = getFmdFromCaptureThread();
            Fmd[] databaseFmds = getFmdsFromDatabase();
            compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
        }

        System.out.println("Identification Thread Stopped");
    }

    private Fmd getFmdFromCaptureThread() throws UareUException, InterruptedException {
        if (onFingerprintScan != null) {
            onFingerprintScan.run();
        }

        captureThread = new CaptureThread("IdentificationCapture", delayTimeInMs, fingerprintLabel);
        captureThread.start();
        captureThread.join();

        CaptureThread.CaptureEvent evt = captureThread.getLastCapture();
        if (evt != null && evt.captureResult.image != null && Reader.CaptureQuality.GOOD == evt.captureResult.quality) {
            return engine.CreateFmd(evt.captureResult.image, Fmd.Format.ISO_19794_2_2005);
        } else {
            System.out.println("Quality:" + evt.captureResult.quality);
            return null;
        }
    }

    private Fmd[] getFmdsFromDatabase() throws UareUException {
        fingerprintList = dao.getFingerprints();
        Fmd[] Fmds = new Fmd[fingerprintList.size()];

        for (int i = 0; i < fingerprintList.size(); i++) {
            byte[] fmdBytes = fingerprintList.get(i).getTemplate();
            if (fmdBytes != null) {
                Fmd importedFmd = UareUGlobal.GetImporter().ImportFmd(
                        fmdBytes,
                        Fmd.Format.ISO_19794_2_2005,
                        Fmd.Format.ISO_19794_2_2005
                );
                Fmds[i] = importedFmd;
            }
        }

        return Fmds;
    }

    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
        if (fmdToIdentify == null) {
            System.out.println("fmdToIdentify is null");
            return false;
        }

        Candidate[] candidateFmds = engine.Identify(fmdToIdentify, 0, databaseFmds, falsePositiveRate, candidateCount);

        if (candidateFmds.length != 0) {
            int topCandidateIndex = candidateFmds[0].fmd_index;
            String matchingUserId = fingerprintList.get(topCandidateIndex).getUser_id();

            if (!headlessMode) {
                userIdentificationSuccess(matchingUserId);
            }

            return true;
        } else {
            if (!headlessMode) {
                userIdentificationFailed();
            }

            return false;
        }
    }

    private FingerprintModel userIdentificationSuccess(String userId) {
        FingerprintModel user = dao.getUserByUserId(userId);
        this.identifiedUser = user; 

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null,
                    "Identification successful for " + user.getFname() + " " + user.getLname());
        });

        try {
            Thread.sleep(delayTimeInMs); 
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, null, ex);
        }

        return user;
    }

    public FingerprintModel getIdentifiedUser() {
        return identifiedUser;
    }

    private void userIdentificationFailed() {

        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(null, "Fingerprint not recognized.");
        });

        try {
            Thread.sleep(1500);
        } catch (InterruptedException ex) {
            Logger.getLogger(IdentificationThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        return compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
    }

    //note it's ArrayList<Fmd> fmdList before, need test
    public boolean fmdIsAlreadyEnrolled(Fmd fmdToIdentify, List<Fmd> fmdList) throws UareUException {
        Fmd[] databaseFmds = getFmdsFromDatabase();
        Fmd[] combinedFmds = new Fmd[databaseFmds.length + fmdList.size()];

        System.arraycopy(databaseFmds, 0, combinedFmds, 0, databaseFmds.length);
        for (int i = 0; i < fmdList.size(); i++) {
            combinedFmds[databaseFmds.length + i] = fmdList.get(i);
        }

        return compareFmdToDatabaseFmds(fmdToIdentify, combinedFmds);
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
}
