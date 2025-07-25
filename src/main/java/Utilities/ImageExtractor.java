package Utilities;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ImageExtractor {

    public static BufferedImage extractBufferedImage(JLabel label) {
        if (label == null || label.getIcon() == null || !(label.getIcon() instanceof ImageIcon)) {
            return null;
        }

        ImageIcon icon = (ImageIcon) label.getIcon();
        Image image = icon.getImage();

        BufferedImage bufferedImage = new BufferedImage(
                image.getWidth(null),
                image.getHeight(null),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    public static byte[] toByteArray(BufferedImage bufferedImage, String format) {
        if (bufferedImage == null || format == null) {
            return null;
        }

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(bufferedImage, format, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace(); 
            return null;
        }
    }

    public static byte[] extractImageBytes(JLabel label, String format) {
        BufferedImage bufferedImage = extractBufferedImage(label);
        return toByteArray(bufferedImage, format);
    }
}
