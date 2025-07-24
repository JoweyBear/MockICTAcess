package Fingerprint;

import com.digitalpersona.uareu.*;

import javax.swing.*;

public class CaptureThread extends Thread {

    private final String threadName;
    private final int delayTimeInMs;
    private final JLabel fingerprintLabel;
    private CaptureEvent lastCapture;
    public boolean isCaptureCanceled = false;
    public boolean runCapture = true;

    public CaptureThread(String threadName, int delayTimeInMs, JLabel fingerprintLabel) {
        this.threadName = threadName;
        this.delayTimeInMs = delayTimeInMs;
        this.fingerprintLabel = fingerprintLabel;
    }

    public CaptureThread(String threadName, int delayTimeInMs) {
        this(threadName, delayTimeInMs, null); 
    }


    public CaptureEvent getLastCapture() {
        return lastCapture;
    }

    @Override
    public void run() {
        if (Selection.isReaderConnected()) {
            startCapture();
        } else {
            System.out.println(threadName + ": Reader not connected. Capture aborted.");
            runCapture = false;
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public void startCapture() {
        System.out.println(threadName + ": Capture Thread Started");

        while (runCapture && Selection.isReaderConnected()) {
            try {
                cancelCaptureBasedOnDelayTime(delayTimeInMs);

                Reader reader = Selection.reader;
                Reader.CaptureResult result = reader.Capture(
                        Fid.Format.ISO_19794_4_2005,
                        Reader.ImageProcessing.IMG_PROC_DEFAULT,
                        reader.GetCapabilities().resolutions[0],
                        -1
                );

                lastCapture = new CaptureEvent(result, reader.GetStatus());
                System.out.println(threadName + ": Capture quality = " + result.quality);

                // Display fingerprint image in Swing JLabel
                Fid.Fiv view = (result.image != null) ? result.image.getViews()[0] : null;
                Display.displayFingerprint(view, fingerprintLabel);

                runCapture = false;

            } catch (UareUException e) {
                e.printStackTrace();
            }
        }

        System.out.println(threadName + ": Capture Thread Stopped");
    }

    public void cancelCaptureBasedOnDelayTime(int delayTimeInMs) {
        if (delayTimeInMs > 0) {
            new Thread(() -> {
                try {
                    System.out.println("Delaying for " + delayTimeInMs + "ms");
                    Thread.sleep(delayTimeInMs);
                    Selection.reader.CancelCapture();
                    isCaptureCanceled = true;
                } catch (InterruptedException | UareUException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    public void stopThread() {
        try {
            Selection.reader.CancelCapture();
        } catch (UareUException e) {
            e.printStackTrace();
        }
        isCaptureCanceled = true;
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
