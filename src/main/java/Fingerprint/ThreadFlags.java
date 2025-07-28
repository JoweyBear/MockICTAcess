package Fingerprint;

public class ThreadFlags {

    public static volatile boolean runIdentificationThread = false;
    public static volatile boolean runVerificationThread = false;
    public static volatile boolean programIsRunning = true;
    public static volatile boolean running = false;

    //flag used by VerificationThread to check if fingerprint is matched
    //public static volatile boolean isFingerprintMatched = false;
}
