package Fingerprint;

import com.digitalpersona.uareu.Fid.Fiv;

import javax.swing.*;
import java.awt.image.BufferedImage;

public class Display {

    public static void displayFingerprint(Fiv view, JLabel label) {
        if (view == null || label == null) {
            System.out.println("Display.displayFingerprint(): Invalid/null input parameters. Cannot display fingerprint.");
            return;
        }

        BufferedImage buffImage = new BufferedImage(
                view.getWidth(),
                view.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY
        );

        buffImage.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());

        label.setIcon(new ImageIcon(buffImage));
    }
}
