package utilities;

import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fmd.Format;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FingerprintCapture {

    private Reader reader;
    private final JLabel imageLabel;
    private Fmd capturedFmd;

    public FingerprintCapture(JLabel imageLabel) {
        this.imageLabel = imageLabel;
    }

    public boolean initializeReader() {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            if (readers.size() == 0) {
                System.out.println("No reader found.");
                return false;
            }
            reader = readers.get(0);
            reader.Open(Reader.Priority.COOPERATIVE);
            System.out.println("Reader opened: " + reader.GetDescription().name);
            return true;
        } catch (UareUException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void closeReader() {
        try {
            if (reader != null) {
                reader.Close();
                System.out.println("Reader closed.");
            }
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    public boolean captureFingerprint() {
        try {
            int dpi = reader.GetCapabilities().resolutions[0];
            Reader.CaptureResult result = reader.Capture(
                    Fid.Format.ANSI_381_2004,
                    Reader.ImageProcessing.IMG_PROC_DEFAULT,
                    dpi,
                    -1 // wait indefinitely
            );

            if (result == null || result.image == null) {
                System.out.println("Capture failed.");
                return false;
            }

            displayImage(result.image);

            Engine engine = UareUGlobal.GetEngine();
            capturedFmd = engine.CreateFmd(result.image, Fmd.Format.DP_PRE_REG_FEATURES);

            System.out.println("Fingerprint captured and FMD created.");
            return true;
        } catch (UareUException e) {
            System.err.println("Capture error: " + e);
            return false;
        }
    }

    private void displayImage(Fid fid) {
        Fid.Fiv view = fid.getViews()[0];
        byte[] imageData = view.getImageData();
        int width = view.getWidth();
        int height = view.getHeight();
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, width, height, imageData);

        ImageIcon icon = new ImageIcon(img.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH));

        SwingUtilities.invokeLater(() -> imageLabel.setIcon(icon));
    }
//
//    public boolean enrollWithPrompt() {
//        try {
//            Engine engine = UareUGlobal.GetEngine();
//            Fmd[] preEnrolledFmds = new Fmd[3];
//
//            for (int i = 0; i < 3; i++) {
//                System.out.println("Please scan finger " + (i + 1) + " of 3...");
//
//                Reader.CaptureResult result = reader.Capture(
//                        Fid.Format.ANSI_381_2004,
//                        Reader.ImageProcessing.IMG_PROC_DEFAULT,
//                        reader.GetCapabilities().resolutions[0],
//                        -1 
//                );
//
//                if (result == null || result.image == null) {
//                    System.out.println("Scan failed. Try again.");
//                    i--; 
//                    continue;
//                }
//
//                displayImage(result.image);
//
//                Fmd fmd = engine.CreateFmd(result.image, Fmd.Format.DP_PRE_REG_FEATURES);
//                preEnrolledFmds[i] = fmd;
//                System.out.println("Scan " + (i + 1) + " completed.");
//            }
//
//            Fmd enrollmentFmd = engine.CreateEnrollmentFmd(Fmd.Format.DP_PRE_REG_FEATURES, preEnrolledFmds);
//            capturedFmd = enrollmentFmd;
//            System.out.println("Enrollment successful.");
//            return true;
//
//        } catch (UareUException e) {
//            System.err.println("Enrollment failed: " + e.getMessage());
//            return false;
//        }
//    }

    public boolean verifyFingerprint(Fmd scannedFmd, Fmd storedFmd) {
        try {
            Engine engine = UareUGlobal.GetEngine();

            int score = engine.Compare(scannedFmd, 0, storedFmd, 0);

            int threshold = 21474;

            System.out.println("Matching score: " + score);
            return score < threshold;

        } catch (UareUException e) {
            System.err.println("Verification error: " + e.getMessage());
            return false;
        }
    }

    public Fmd getCapturedFmd() {
        return capturedFmd;
    }
}
