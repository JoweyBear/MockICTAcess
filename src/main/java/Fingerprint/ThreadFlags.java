package Fingerprint;

public class ThreadFlags {

    public static volatile boolean runIdentificationThread = false;
    public static volatile boolean runVerificationThread = false;
    public static volatile boolean programIsRunning = true;
    public static volatile boolean running = true;
    public static volatile boolean captureInProgress = false;


    //flag used by VerificationThread to chesrprintMatched = false;
}
