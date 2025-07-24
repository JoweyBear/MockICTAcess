package Fingerprint;

public class ThreadFlags {

    public static volatile boolean runIdentificationThread = false;
    public static volatile boolean runVerificationThread = false;
    public static volatile boolean programIsRunning = true;

    //flag used by VerificationThread to check if fingerprint is matched
    //public static volatile boolean isFingerprintMatched = false;
}
