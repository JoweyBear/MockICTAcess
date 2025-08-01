package Fingerprint;

import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Selection extends Thread {

    public static Reader reader;

    public static ReaderCollection getReaderCollection() throws UareUException {
        ReaderCollection readerCollection = UareUGlobal.GetReaderCollection();
        readerCollection.GetReaders();

        if (readerCollection == null) {
            System.out.println("getReaderCollection: ReaderCollection is null!");
        }
        return readerCollection;
    }

    public static boolean readerIsConnected() {
        try {
            ReaderCollection readerCollection = getReaderCollection();
            if (readerCollection == null) {
                System.out.println("readerIsConnected: ReaderCollection is null!");
                return false;
            }
            if (!readerCollection.isEmpty()) {
                Reader reader = readerCollection.get(0);
                if (reader == null) {
                    System.out.println("readerIsConnected: Reader is null!");
                    return false;
                }
                System.out.println("readerIsConnected method: Connected fingerprint reader: " + reader.GetDescription().name);
                return true;
            } else {
                System.out.println("No fingerprint reader found.");
            }
        } catch (UareUException e) {
            System.out.println("readerIsConnected: UareUException occurred!");
            e.printStackTrace();
        }
        return false;
    }

    public static boolean readerIsConnected_noLogging() {
        try {
            ReaderCollection readerCollection = getReaderCollection();
            if (readerCollection == null) {
                return false;
            }
            if (!readerCollection.isEmpty()) {
                Reader reader = readerCollection.get(0);
                if (reader == null) {
                    return false;
                }
                return true;
            }
        } catch (UareUException e) {
            // No logging as per method name
        }
        return false;
    }

    public static void getReader() {
        try {
            if (readerIsConnected()) {
                ReaderCollection readerCollection = getReaderCollection();
                if (readerCollection == null || readerCollection.isEmpty()) {
                    System.out.println("getReader: ReaderCollection is null or empty!");
                    reader = null;
                    return;
                }
                reader = readerCollection.get(0);
                if (reader == null) {
                    System.out.println("getReader: Reader is null after get(0)!");
                }
            }
        } catch (UareUException e) {
            System.out.println("getReader: Exception occurred!");
            e.printStackTrace();
        }
    }

    public static void resetReader() {
        try {
            ReaderCollection readers = getReaderCollection();
            if (readers != null && !readers.isEmpty()) {
                if (reader != null) {
                    try {
                        reader.Close(); 
                    } catch (UareUException e) {
                        System.out.println("resetReader: Failed to close old reader.");
                    }
                }
                reader = readers.get(0); 
                reader.Open(Reader.Priority.COOPERATIVE);
                System.out.println("resetReader: Reader has been reset and reopened.");
            } else {
                System.out.println("resetReader: No readers found.");
            }
        } catch (UareUException e) {
            System.out.println("resetReader: Exception occurred.");
            e.printStackTrace();
        }
    }

    public static void closeAndOpenReader() throws UareUException {
        if (readerIsConnected_noLogging()) {
            if (reader == null) {
                System.out.println("closeAndOpenReader: reader is null!");
                return;
            }
            try {
                reader.Close();
                reader.Open(Reader.Priority.COOPERATIVE);
            } catch (UareUException ex) {
                System.out.println("closeAndOpenReader: Exception on Close(), retrying Open()");
                reader.Open(Reader.Priority.COOPERATIVE);
            }
        } else {
            System.out.println("closeAndOpenReader: No reader connected!");
        }
    }

    public static void closeReader() throws UareUException {
        if (readerIsConnected_noLogging()) {
            if (reader == null) {
                System.out.println("closeReader: reader is null!");
                return;
            }
            try {
                reader.Close();
            } catch (UareUException ex) {
                System.out.println("closeReader: Exception on Close(), retrying Open()");
                reader.Open(Reader.Priority.COOPERATIVE);
            }
        } else {
            System.out.println("closeReader: No reader connected!");
        }
    }

    public static void waitAndGetReader() {
        ExecutorService executor = Executors.newFixedThreadPool(1);
        executor.execute(() -> {
            while (true && ThreadFlags.programIsRunning) {
                getReader();
                if (reader == null) {
                    System.out.println("No fingerprint reader found. Waiting for a reader to be connected...");
                } else {
                    System.out.println("Connected fingerprint reader: " + reader.GetDescription().name);
                    //                        closeAndOpenReader();
                    resetReader();
                }

                try {
                    TimeUnit.SECONDS.sleep(5); // You can adjust the sleep duration as needed
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        // Do not shutdown executor here, keep thread running for detection
    }

    @Override
    public void run() {
        waitAndGetReader();
    }
}
