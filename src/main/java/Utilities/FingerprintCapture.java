package utilities;

import com.digitalpersona.javapos.services.biometrics.Capture;
import com.digitalpersona.uareu.*;
import com.digitalpersona.uareu.Fmd.Format;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FingerprintCapture {

    private Reader reader;
    private Fmd capturedFmd;
    private Fid capturedFid;
    private byte[] fingerprintImageBytes;
    private int imageWidth;
    private int imageHeight;

    public boolean initializeReader() {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            if (readers.size() == 0) {
                System.out.println("No reader found.");
                return false;
            }
            reader = readers.get(0);
            reader.Open(Reader.Priority.EXCLUSIVE);
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

            capturedFid = result.image;
            Fid.Fiv view = result.image.getViews()[0];
            fingerprintImageBytes = view.getImageData();
            imageWidth = view.getWidth();
            imageHeight = view.getHeight();
//            displayImage(view);

            Engine engine = UareUGlobal.GetEngine();
            capturedFmd = engine.CreateFmd(result.image, Fmd.Format.DP_PRE_REG_FEATURES);

            System.out.println("Fingerprint captured and FMD created.");
            return true;
        } catch (UareUException e) {
            System.err.println("Capture error: " + e);
            return false;
        }
    }

    public Reader.CaptureResult captureOnce() {
        try {
            int dpi = reader.GetCapabilities().resolutions[0];

            Reader.CaptureResult result = reader.Capture(
                    Fid.Format.ANSI_381_2004,
                    Reader.ImageProcessing.IMG_PROC_DEFAULT,
                    dpi,
                    -1 // wait indefinitely
            );
        } catch (UareUException ex) {
            Logger.getLogger(FingerprintCapture.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
//    private void displayImage(Fid.Fiv view) {
//        BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//        img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
//
//        ImageIcon icon = new ImageIcon(img.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH));
//        SwingUtilities.invokeLater(() -> imageLabel.setIcon(icon));
//    }

    public boolean verifyFingerprint(Fmd scannedFmd, Fmd storedFmd) {
        System.out.println("called verify fingerprint");
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
//
//    public void showFingerprintOnLabel(JLabel targetLabel) {
//        if (fingerprintImageBytes == null || imageWidth <= 0 || imageHeight <= 0) {
//            System.out.println("No fingerprint image data to display.");
//            return;
//        }
//
//        BufferedImage img = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_BYTE_GRAY);
//        img.getRaster().setDataElements(0, 0, imageWidth, imageHeight, fingerprintImageBytes);
//
//        ImageIcon icon = new ImageIcon(img.getScaledInstance(targetLabel.getWidth(), targetLabel.getHeight(), Image.SCALE_SMOOTH));
//
//        SwingUtilities.invokeLater(() -> targetLabel.setIcon(icon));
//    }

    public boolean matchFingerprint(byte[] storedTemplateBytes, Fmd scannedFmd) {
        try {
            // Import stored template bytes into an Fmd object
            Fmd storedFmd = UareUGlobal.GetImporter()
                    .ImportFmd(
                            storedTemplateBytes,
                            Fmd.Format.DP_PRE_REG_FEATURES, // stored format
                            Fmd.Format.DP_PRE_REG_FEATURES
                    );

            Engine engine = UareUGlobal.GetEngine();

            // Identify: compare scannedFmd against array of one stored Fmd
            Fmd[] storedArray = new Fmd[]{storedFmd};
            Engine.Candidate[] candidates = engine.Identify(
                    scannedFmd,
                    0, // max FAR (zero for strict match)
                    storedArray,
                    1,
                    storedArray.length
                    
            );

            return candidates.length > 0;  // match found

        } catch (UareUException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Fmd capture() {
        if (!initializeReader()) {
            return null;
        }
        if (captureFingerprint()) {
            closeReader(); // optional
            return capturedFmd;
        }
        closeReader();
        return null;
    }

    public void clearCapturedData() {
        capturedFmd = null;
        capturedFid = null;
        fingerprintImageBytes = null;
    }

    public Fmd getCapturedFmd() {
        return capturedFmd;
    }

    public Fid getCapturedFid() {
        return capturedFid;
    }

    public byte[] getFingerprintImageBytes() {
        return fingerprintImageBytes;
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public int getImageHeight() {
        return imageHeight;
    }
}
