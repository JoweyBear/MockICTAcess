package Utilities;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;

public class ImageUploader {

    public byte[] pickImage(JPanel panel, JLabel image) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Image");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                "Image files", "jpg", "jpeg", "png", "gif"));

        int result = chooser.showOpenDialog(panel);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();

            ImageIcon previewIcon = new ImageIcon(selectedFile.getAbsolutePath());
            Image scaled = previewIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel preview = new JLabel(new ImageIcon(scaled));
            int confirm = JOptionPane.showConfirmDialog(panel, preview, "Preview Image", JOptionPane.OK_CANCEL_OPTION);
            if (confirm != JOptionPane.OK_OPTION) {
                return null;
            }

            try {
                byte[] imageBytes = Files.readAllBytes(selectedFile.toPath());

                image.setText("");
                image.setIcon(new ImageIcon(scaled));
                image.setHorizontalAlignment(SwingConstants.CENTER);
                image.setVerticalAlignment(SwingConstants.CENTER);

                return imageBytes;

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(panel, "Error reading image: " + ex.getMessage());
            }
        }
        return null;
    }
}
