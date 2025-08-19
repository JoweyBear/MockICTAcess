package Fingerprint;

import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.UareUException;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class CaptureThread extends Thread {

    private final String threadName;
    private final JLabel fingerprintLabel;
    private final int chunkTimeoutMs;
    private volatile boolean runCapture = true;
    private volatile boolean isCaptureCanceled = false;

    private CaptureListener listener;
    private CaptureEvent lastCapture;

    
    public CaptureThread(String threadName, int chunkTimeoutMs, JLabel fingerprintLabel) {
        this.threadName = threadName;
        this.chunkTimeoutMs = chunkTimeoutMs;
        this.fingerprintLabel = fingerprintLabel;
        setName(threadName);
        setDaemon(true);
    }

    public void setCaptureListener(CaptureListener listener) {
        this.listener = listener;
    }

    public void stopThread() {
        System.out.println(threadName + " - stopThread() called: setting flags and sending CancelCapture()");
        runCapture = false;
        isCaptureCanceled = true;
        try {
            if (Selection.reader != null) {
                Selection.reader.CancelCapture(); // wakes blocking capture
            } else {
                System.out.println(threadName + " - stopThread(): reader is null");
            }
        } catch (UareUException e) {
            System.out.println(threadName + " - stopThread(): CancelCapture threw an exception");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println(threadName + " - Thread started");

        if (!Selection.readerIsConnected_noLogging()) {
            System.out.println(threadName + " - Reader not connected, exiting thread");
            return;
        }

        if (listener != null) {
            listener.onCaptureProgress("Starting capture...", 10);
        }

        while (runCapture && Selection.readerIsConnected_noLogging()) {
            try {
                if (listener != null) {
                    listener.onCaptureProgress("Capturing...", 30);
                }
                System.out.println(threadName + " - Calling reader.Capture() with timeout=" + chunkTimeoutMs + "ms");

                int dpi = Selection.reader.GetCapabilities().resolutions[0];
                Reader.CaptureResult result = Selection.reader.Capture(
                        Fid.Format.ANSI_381_2004,
                        Reader.ImageProcessing.IMG_PROC_DEFAULT,
                        dpi,
                        chunkTimeoutMs
                );

                // Null result → likely timeout
                if (result == null) {
                    System.out.println(threadName + " - Capture returned null (timeout), retrying...");
                    continue;
                }

                System.out.println(threadName + " - Capture quality: " + result.quality);

                // Get reader status
                try {
                    lastCapture = new CaptureEvent(result, Selection.reader.GetStatus());
                } catch (UareUException stEx) {
                    System.out.println(threadName + " - Failed to get reader status");
                    stEx.printStackTrace();
                }

                // Handle no image cases
                if (result.image == null) {
                    System.out.println(threadName + " - No image in result");
                    if (result.quality == Reader.CaptureQuality.TIMED_OUT
                            || result.quality == Reader.CaptureQuality.NO_FINGER) {
                        continue;
                    }
                    if (result.quality == Reader.CaptureQuality.CANCELED) {
                        System.out.println(threadName + " - Capture was canceled");
                        isCaptureCanceled = true;
                        break;
                    }
                    // For any other non-good, retry
                    continue;
                }

                // Extract first view
                Fid.Fiv[] views = result.image.getViews();
                if (views == null || views.length == 0) {
                    System.out.println(threadName + " - No views found in image");
                    continue;
                }
                Fid.Fiv view = views[0];

                // Update UI
                BufferedImage img = Display.getFingerprintBufferedImage(view);
                if (img != null && fingerprintLabel != null) {
                    SwingUtilities.invokeLater(() -> {
                        fingerprintLabel.setIcon(new ImageIcon(img));
                    });
                }

                if (result.quality != Reader.CaptureQuality.GOOD) {
                    System.out.println(threadName + " - Non-GOOD quality (" + result.quality + "), retrying...");
                    continue;
                }

                // Successful scan
                if (listener != null) {
                    listener.onCaptureImage(view);
                    listener.onCaptureProgress("Capture complete!", 100);
                    listener.onCaptureComplete(lastCapture);
                }
                System.out.println(threadName + " - GOOD quality capture complete, exiting loop");
                runCapture = false;

            } catch (UareUException e) {
                System.out.println(threadName + " - Exception during capture");
                e.printStackTrace();
                if (listener != null) {
                    listener.onCaptureError(e);
                }
                if (!runCapture) break; // exit on stop request
            }
        }

        System.out.println(threadName + " - Thread ending");
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
        public final Reader.CaptureResult captureResult;
        public final Reader.Status readerStatus;

        public CaptureEvent(Reader.CaptureResult captureResult, Reader.Status readerStatus) {
            this.captureResult = captureResult;
            this.readerStatus = readerStatus;
        }
    }
}