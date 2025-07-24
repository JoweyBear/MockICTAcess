package Fingerprint;

import com.digitalpersona.uareu.*;

public class Selection extends Thread {

    public static volatile Reader reader;

    // Get the first available reader
    public static Reader getReader() {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();

            if (!readers.isEmpty()) {
                reader = readers.get(0);
                return reader;
            }
        } catch (UareUException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Check if a reader is connected (silent)
    public static boolean isReaderConnected() {
        try {
            ReaderCollection readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();
            return !readers.isEmpty();
        } catch (UareUException e) {
            return false;
        }
    }

    // Open the reader safely
    public static void openReader() {
        try {
            if (reader != null) {
                reader.Open(Reader.Priority.COOPERATIVE);
            }
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    // Close the reader safely
    public static void closeReader() {
        try {
            if (reader != null) {
                reader.Close();
            }
        } catch (UareUException e) {
            e.printStackTrace();
        }
    }

    // Reopen the reader (used for recovery)
    public static void resetReader() {
        try {
            closeReader();
            openReader();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 🧭 Restored: continuously monitor for reader connection
    public static void waitAndGetReader() {
        while (ThreadFlags.programIsRunning) {
            getReader();
            if (reader == null) {
                System.out.println("No fingerprint reader found. Waiting for a reader to be connected...");
            } else {
                System.out.println("Connected fingerprint reader: " + reader.GetDescription().name);
                resetReader();
            }

            try {
                Thread.sleep(5000); // Wait 5 seconds before checking again
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // 🧵 Thread entry point
    @Override
    public void run() {
        waitAndGetReader();
    }
}
