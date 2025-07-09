package Utilities;

import com.digitalpersona.onetouch.*;
import com.digitalpersona.onetouch.capture.*;
import com.digitalpersona.onetouch.capture.event.*;
import com.digitalpersona.onetouch.processing.DPFPImageQualityException;
import java.awt.Image;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class FingerprintCapture {

//    private DPFPCapture capturer;
//    private DPFPTemplate template;
//    private JLabel displayLabel;
//    private DPFPSample sample;  
//
//    public FingerprintCapture(JLabel displayLabel) {
//        this.displayLabel = displayLabel;
//        this.capturer = DPFPGlobal.getCaptureFactory().createCapture();
//
//        this.capturer.addDataListener(new DPFPDataAdapter() {
//            @Override
//            public void dataAcquired(final DPFPDataEvent e) {
//                sample = e.getSample();  
//                DPFPFeatureSet features = extractFeatures(sample);
//                if (features != null) {
//                    template = DPFPGlobal.getTemplateFactory().createTemplate(features);
//                    showFingerprint(sample);
//                    System.out.println("Fingerprint captured!");
//                }
//            }
//        });
//
//        this.capturer.addReaderStatusListener(new DPFPReaderStatusAdapter() {
//            @Override
//            public void readerConnected(DPFPReaderStatusEvent e) {
//                System.out.println("Fingerprint reader connected.");
//            }
//
//            @Override
//            public void readerDisconnected(DPFPReaderStatusEvent e) {
//                System.out.println("Fingerprint reader disconnected.");
//            }
//        });
//    }
//
//    public void startCapture() {
//        capturer.startCapture();
//        System.out.println("Place your finger on the scanner.");
//    }
//
//    public void stopCapture() {
//        capturer.stopCapture();
//        System.out.println("Capture stopped.");
//    }
//
//    public DPFPTemplate getTemplate() {
//        return template;
//    }
//
//    public DPFPSample getSample() {
//        return sample;
//    }
//
//    private void showFingerprint(DPFPSample sample) {
//        Image img = DPFPGlobal.getSampleConversionFactory().createImage(sample);
//        displayLabel.setIcon(new ImageIcon(img.getScaledInstance(displayLabel.getWidth(), displayLabel.getHeight(), Image.SCALE_DEFAULT)));
//    }
//
//    private DPFPFeatureSet extractFeatures(DPFPSample sample) {
//        try {
//            return DPFPGlobal.getFeatureExtractionFactory()
//                    .createFeatureExtraction()
//                    .createFeatureSet(sample, DPFPDataPurpose.DATA_PURPOSE_ENROLLMENT);
//        } catch (DPFPImageQualityException ex) {
//            Logger.getLogger(FingerprintCapture.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return sample;
//    }
}

