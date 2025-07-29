package Fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;

import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class CaptureThread extends Thread {

    private String threadName;
    private JLabel fingerprintLabel;
    private int delayTimeInMs;
    private boolean runCapture = true;
    boolean isCaptureCanceled = false;

    private CaptureListener listener;
    private CaptureEvent lastCapture;

    public CaptureThread(String threadName) {
        this.threadName = threadName;
    }

    public CaptureThread(JLabel fingerprintLabel) {
        this.fingerprintLabel = fingerprintLabel;
    }

    public CaptureThread(String threadName, int delayTimeInMs, JLabel fingerprintLabel) {
        this.threadName = threadName;
        this.delayTimeInMs = delayTimeInMs;
        this.fingerprintLabel = fingerprintLabel;
    }

    public void setCaptureListener(CaptureListener listener) {
        this.listener = listener;
    }

    public void stopThread() {
        try {
            if (Selection.reader == null) {
                System.out.println("stopThread: Selection.reader is null!");
                return;
            }
            Selection.reader.CancelCapture();
        } catch (UareUException e) {
            System.out.println("stopThread: Exception during CancelCapture.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        isCaptureCanceled = true;
    }

    @Override
    public void run() {
        System.out.println("CaptureThread '" + threadName + "' starting...");
        if (Selection.readerIsConnected_noLogging()) {
            startCapture();
        } else {
            System.out.println(threadName + ": Reader not connected.");
            runCapture = false;
        }
    }

    public void startCapture() {
        if (listener != null) {
            listener.onCaptureProgress("Starting capture...", 10);
        }

        while (runCapture && Selection.readerIsConnected_noLogging()) {
            try {
                if (listener != null) {
                    listener.onCaptureProgress("Capturing...", 30);
                }

                cancelCaptureBasedOnDelayTime(delayTimeInMs);

                // Parameter checks before native call
                if (Selection.reader == null) {
                    System.out.println("startCapture: Selection.reader is null!");
                    break;
                }
                if (Selection.reader.GetCapabilities() == null) {
                    System.out.println("startCapture: Selection.reader.GetCapabilities() is null!");
                    break;
                }
                if (Selection.reader.GetCapabilities().resolutions == null
                        || Selection.reader.GetCapabilities().resolutions.length == 0) {
                    System.out.println("startCapture: No resolutions available in reader capabilities!");
                    break;
                }

                System.out.println("About to call Selection.reader.Capture with format ANSI_381_2004, resolution: "
                        + Selection.reader.GetCapabilities().resolutions[0]);

                Reader.CaptureResult captureResult = Selection.reader.Capture(
                        Fid.Format.ANSI_381_2004,
                        Reader.ImageProcessing.IMG_PROC_DEFAULT,
                        Selection.reader.GetCapabilities().resolutions[0],
                        -1
                );

                if (captureResult == null) {
                    System.out.println("startCapture: CaptureResult is null!");
                } else {
                    System.out.println("CaptureResult quality: " + captureResult.quality);
                }

                lastCapture = new CaptureEvent(captureResult, Selection.reader.GetStatus());

                Fid.Fiv view = null;
                if (captureResult != null && captureResult.image != null) {
                    Fid.Fiv[] views = captureResult.image.getViews();
                    if (views != null && views.length > 0) {
                        view = views[0];
                        BufferedImage img = Display.getFingerprintBufferedImage(view);
                        if (img != null && fingerprintLabel != null) {
                            SwingUtilities.invokeLater(() -> {
                                fingerprintLabel.setIcon(new ImageIcon(img));
                            });
                        }
                    } else {
                        System.out.println("startCapture: No views available in captureResult.image!");
                    }
                } else {
                    System.out.println("startCapture: captureResult or captureResult.image is null!");
                }

                if (listener != null) {
                    listener.onCaptureImage(view);
                    listener.onCaptureProgress("Capture complete!", 100);
                    listener.onCaptureComplete(lastCapture);
                }

                runCapture = false;

            } catch (UareUException e) {
                System.out.println("startCapture: Exception during capture.");
                e.printStackTrace();
                if (listener != null) {
                    listener.onCaptureError(e);
                }
            }
        }

        System.out.println(threadName + ": Capture finished.");
    }

    public void cancelCaptureBasedOnDelayTime(int delayTimeInMs) {
        if (delayTimeInMs != 0) {
            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    Thread.sleep(delayTimeInMs);
                    if (Selection.reader == null) {
                        System.out.println("cancelCaptureBasedOnDelayTime: Selection.reader is null!");
                        return;
                    }
                    Selection.reader.CancelCapture();
                    isCaptureCanceled = true;
                } catch (Exception e) {
                    System.out.println("cancelCaptureBasedOnDelayTime: Exception during CancelCapture.");
                    e.printStackTrace();
                }
            });
            executor.shutdown();
        }
    }

    public CaptureEvent getLastCapture() {
        return lastCapture;
    }

    public interface CaptureListener {

        void onCaptureProgress(String message, int percent);

        void onCaptureImage(Fid.Fiv fingerprintImage);

        void onCaptureComplete(CaptureEvent event);

        void onCaptureError(Exception e);
    }

    public static class CaptureEvent {

        public Reader.CaptureResult captureResult;
        public Reader.Status readerStatus;

        public CaptureEvent(Reader.CaptureResult captureResult, Reader.Status readerStatus) {
            this.captureResult = captureResult;
            this.readerStatus = readerStatus;
        }
    }
}
