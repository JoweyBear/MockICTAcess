package Utilities;

import Main.Views.MainFrame;
import Main.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Locale;

public class Time {

    MainFrame frame;
    DateFormat df = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
    DateFormat tf = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);

    public Time(MainFrame frame) {
        this.frame = frame;
    }

    public void setTime() {
        Worker worker = new Worker();
        worker.start();
    }

    class Worker extends Thread {

        @Override
        public void run() {
            while (true) {
                Date dateobj = new Date();
                frame.date.setText(df.format(dateobj));
                frame.time.setText(tf.format(dateobj));

                // Sleep for a while
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // Interrupted exception will occur if
                    // the Worker object's interrupt() method
                    // is called. interrupt() is inherited
                    // from the Thread class.
                    break;
                }
            }
        }
    }

}
