/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Fingerprint;

/**
 *
 * @author Administrator
 */
public class oldUsage {
//            JDialog progressDialog = new JDialog((JFrame) null, "Scan Fingerprint", true);
//        JProgressBar progressBar = new JProgressBar(0, 3);
//        progressBar.setValue(0);
//        progressBar.setString("Scan 1 of 3");
//        progressBar.setStringPainted(true);
//        progressDialog.add(progressBar);
//        progressDialog.setSize(300, 75);
//        progressDialog.setLocationRelativeTo(null);
//
//        SwingWorker<Void, Void> worker = new SwingWorker<>() {
//            final int requiredScans = 3;
//            final Fmd[] preEnrollFmds = new Fmd[requiredScans];
//            int scanCount = 0;
//            boolean allSuccessful = true;
//            Fid finalFid = null;
//
//            @Override
//            protected Void doInBackground() {
//                if (!scanner.initializeReader()) {
//                    JOptionPane.showMessageDialog(null, "Failed to initialize fingerprint reader.");
//                    allSuccessful = false;
//                    return null;
//                }
//
//                while (scanCount < requiredScans) {
//                    progressBar.setValue(scanCount);
//                    progressBar.setString("Scan " + (scanCount + 1) + " of " + requiredScans);
//
//                    boolean captured = scanner.captureFingerprint();
//                    if (!captured) {
//                        allSuccessful = false;
//                        break;
//                    }
//
//                    Fmd fmd = scanner.getCapturedFmd();
//                    if (fmd != null) {
//                        preEnrollFmds[scanCount] = fmd;
//                        finalFid = scanner.getCapturedFid();
//                        scanCount++;
//                    }
//                }
//                progressBar.setValue(requiredScans);
//                progressBar.setString("Generating final fingerprint...");
//                if (scanCount == requiredScans) {
//                    Fmd finalFmd = preEnrollFmds[requiredScans - 1];
//                    fingerprintTemplateAdd = finalFmd.getData();
//                } else {
//                    allSuccessful = false;
//                }
//                scanner.closeReader();
//
//                return null;
//            }
//
//            @Override
//            protected void done() {
//                progressDialog.dispose();
//
//                if (allSuccessful) {
//                    JOptionPane.showMessageDialog(null, "Fingerprint enrollment successful!");
//
//                    if (finalFid != null) {
//                        Fid.Fiv view = finalFid.getViews()[0];
//                        BufferedImage img = new BufferedImage(view.getWidth(), view.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//                        img.getRaster().setDataElements(0, 0, view.getWidth(), view.getHeight(), view.getImageData());
//                        ImageIcon icon = new ImageIcon(img.getScaledInstance(addPanel.jLabelfinger.getWidth(),
//                                addPanel.jLabelfinger.getHeight(), Image.SCALE_SMOOTH));
//                        SwingUtilities.invokeLater(() -> {
//                            addPanel.jLabelfinger.setText("");
//                            addPanel.jLabelfinger.setIcon(icon);
//                        });
//
//                        try {
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            ImageIO.write(img, "png", baos);
//                            fingerprintImageAdd = baos.toByteArray();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                            JOptionPane.showMessageDialog(null, "Error saving image: " + e.getMessage());
//                        }
//                    }
//
//                } else {
//                    JOptionPane.showMessageDialog(null, "Fingerprint enrollment failed.");
//                }
//            }
//        };
//
//        worker.execute();
//        progressDialog.setVisible(true);
}
