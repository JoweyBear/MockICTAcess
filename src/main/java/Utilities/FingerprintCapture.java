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
    private byte[] fingerprintImageBytes;
    private int imageWidth;
    private int imageHeight;

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

            // Extract image for GUI and storage
            Fid.Fiv view = result.image.getViews()[0];
            fingerprintImageBytes = view.getImageData();
            imageWidth = view.getWidth();
            imageHeight = view.getHeight();
            displayImage(view);

            // Extract FMD template
            Engine engine = UareUGlobal.GetEngine();
            capturedFmd = engine.CreateFmd(result.image, Fmd.Format.DP_PRE_REG_FEATURES);

            System.out.println("Fingerprint captured and FMD created.");
            return true;
        } catch (UareUException e) {
            System.err.println("Capture error: " + e);
            return false;
        }
    }

    private void displayImage(Fid.Fiv view) {
        BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
        img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());

        ImageIcon icon = new ImageIcon(img.getScaledInstance(imageLabel.getWidth(), imageLabel.getHeight(), Image.SCALE_SMOOTH));
        SwingUtilities.invokeLater(() -> imageLabel.setIcon(icon));
    }

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
