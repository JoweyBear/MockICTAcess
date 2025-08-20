package Fingerprint;

import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Engine.Candidate;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

public class AttendanceThread {

    private volatile boolean runThisThread = true;
    private FingerprintModel identifiedUser;
    private final Engine engine = UareUGlobal.GetEngine();
    private final FingerprintDAO dao = new FingerprintDAOImpl();
    private final boolean headlessMode = false;

    private Reader reader;

    private List<FingerprintModel> fingerprintList;
    int falsePositiveRate = Engine.PROBABILITY_ONE / 100000;
    private final int candidateCount = 1;
    int retryCount = 0;
    int maxRetries = 20; // e.g., 30 seconds total

    private JProgressBar progressBar;

    // A blocking queue to receive FMDs from the capture service
    private final BlockingQueue<Fmd> fmdQueue = new LinkedBlockingQueue<>();

    public AttendanceThread(JProgressBar progressBar) {
        this.progressBar = progressBar;
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            if (readers.size() == 0) {
                throw new RuntimeException("No fingerprint reader found.");
            }
            reader = readers.get(0);
            reader.Open(Reader.Priority.COOPERATIVE);
        } catch (UareUException e) {
            throw new RuntimeException("Failed to initialize reader", e);
        }
    }

    public void onFingerprintCaptured() {

        while (Selection.readerIsConnected_noLogging()) {
            try {
                if (reader == null) {
                    System.out.println("startCapture: Selection.reader is null!");
                    break;
                }
                if (reader.GetCapabilities() == null) {
                    System.out.println("startCapture: Selection.reader.GetCapabilities() is null!");
                    break;
                }
                if (reader.GetCapabilities().resolutions == null
                        || reader.GetCapabilities().resolutions.length == 0) {
                    System.out.println("startCapture: No resolutions available in reader capabilities!");
                    break;
                }
                while (retryCount < maxRetries) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Capture thread interrupted");
                        break;
                    }

                    Reader.CaptureResult captureResult = reader.Capture(
                            Fid.Format.ANSI_381_2004,
                            Reader.ImageProcessing.IMG_PROC_DEFAULT,
                            reader.GetCapabilities().resolutions[0],
                            -1
                    );

                    if (captureResult == null) {
                        retryCount++;
                        System.out.println("startCapture: CaptureResult is retrying! Attempt #" + retryCount);
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setIndeterminate(true);
                            progressBar.setString("Waiting for fingerprint...");
                        });
                        Thread.sleep(500);
                        continue;
                    }

                    System.out.println("CaptureResult quality: " + captureResult.quality);

                    if (captureResult.image != null) {
                        SwingUtilities.invokeLater(() -> {
                            progressBar.setIndeterminate(false);
                            progressBar.setValue(100);
                            progressBar.setString("Fingerprint detected");
                        });

                        Fmd fmd = engine.CreateFmd(captureResult.image, Fmd.Format.ANSI_378_2004);
                        if (fmd != null) {
                            fmdQueue.offer(fmd);
                        }
                    }

                    break; // successful capture, exit loop
                }

                if (retryCount >= maxRetries) {
                    SwingUtilities.invokeLater(() -> {
                        progressBar.setIndeterminate(false);
                        progressBar.setValue(0);
                        progressBar.setString("Capture timed out");
                    });
                }

            } catch (UareUException | InterruptedException ex) {
                Logger.getLogger(AttendanceThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void startAttendance() throws InterruptedException, UareUException {
        System.out.println("Identification Thread Started");

        while (runThisThread) {
            // Wait for next fingerprint from the queue
            Fmd fmdToIdentify = fmdQueue.take();

            if (!isValidFmd(fmdToIdentify, "startIdentification: fmdToIdentify")) {
                continue;
            }
            Fmd[] databaseFmds = getFmdsFromDatabase();

            if (!isValidFmdArray(databaseFmds, "startIdentification: databaseFmds")) {
                continue;
            }
            compareFmdToDatabaseFmds(fmdToIdentify, databaseFmds);
        }

        System.out.println("Identification Thread Stopped");
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
                continue;
            }
            try {
                Fmd importedFmd = UareUGlobal.GetImporter().ImportFmd(
                        fmdBytes,
                        Fmd.Format.ANSI_378_2004,
                        Fmd.Format.ANSI_378_2004
                );
                if (isValidFmd(importedFmd, "getFmdsFromDatabase: ImportFmd[" + i + "]")) {
                    Fmds[validCount++] = importedFmd;
                }
            } catch (UareUException e) {
                e.printStackTrace();
            }
        }
        return Arrays.copyOf(Fmds, validCount);
    }

    private boolean compareFmdToDatabaseFmds(Fmd fmdToIdentify, Fmd[] databaseFmds) throws UareUException {
        Candidate[] candidateFmds;
        try {
            candidateFmds = engine.Identify(fmdToIdentify, 0, databaseFmds, falsePositiveRate, candidateCount);
        } catch (UareUException e) {
            e.printStackTrace();
            return false;
        }

        if (candidateFmds != null && candidateFmds.length > 0) {
            int topCandidateFmdIndex = candidateFmds[0].fmd_index;
            String matchingUserId = fingerprintList.get(topCandidateFmdIndex).getUser_id();
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
        runThisThread = false;
        System.out.println("Identification Success: " + (user != null ? user.getFname() : "Unknown User"));
        return user;
    }

    private void userIdentificationFailed() {
        this.identifiedUser = null;
        runThisThread = false;
        System.out.println("Identification failed: Unknown fingerprint.");
    }

    public FingerprintModel getIdentifiedUser() {
        return identifiedUser;
    }

    public void shutdown() {
        runThisThread = false;
        if (reader != null) {
            try {
                reader.CancelCapture();
                reader.Close();
            } catch (UareUException e) {
                e.printStackTrace();
            }
        }
    }

    // ---- validation methods unchanged ----
    private boolean isValidFmd(Fmd fmd, String context) {
        return fmd != null && fmd.getData() != null && fmd.getData().length >= 100;
    }

    private boolean isValidFmdArray(Fmd[] fmds, String context) {
        return fmds != null && fmds.length > 0;
    }
}
