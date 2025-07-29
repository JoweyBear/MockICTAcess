package Fingerprint;

import com.digitalpersona.uareu.Fid.Fiv;

import java.awt.image.BufferedImage;

public class Display {
    public static BufferedImage getFingerprintBufferedImage(Fiv view) {
        if (view == null || view.getImageData() == null) return null;

        byte[] imageData = view.getImageData();
        int width = view.getWidth();
        int height = view.getHeight();

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        image.getRaster().setDataElements(0, 0, width, height, imageData);
        return image;
    }
}