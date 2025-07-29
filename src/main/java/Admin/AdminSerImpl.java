package Admin;

import Admin.Views.*;
import Fingerprint.EnrollmentThread;
import Fingerprint.FingerprintModel;
import Fingerprint.PromptSwing;
import Fingerprint.Selection;
import Utilities.ImageExtractor;
import Utilities.ImageUploader;
import Utilities.SearchDefaultModel;
import com.digitalpersona.uareu.Compression;
import com.digitalpersona.uareu.Engine;
import com.digitalpersona.uareu.Fid;
import com.digitalpersona.uareu.Fmd;
import com.digitalpersona.uareu.Reader;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import utilities.FingerprintCapture;

public class AdminSerImpl implements AdminService {

    AdminDAO dao = new AdminDAOImpl();
    AdminPanel adminPanel;
    AddAdPanel addPanel;
    EditAdPanel editPanel;
    ViewAdminDialog viewDialog;
    private final ImageUploader imageUploader = new ImageUploader();
    private byte[] uploadedImageForAdd;
    private byte[] uploadedImageForEdit;
    private byte[] fingerprintTemplateAdd;
    private byte[] fingerprintImageAdd;
    private byte[] fingerprintTemplateEdit;
    private byte[] fingerprintImageEdit;
    FingerprintCapture scanner = new FingerprintCapture();

    public AdminSerImpl(AdminPanel adminPanel, AddAdPanel addPanel, EditAdPanel editPanel, ViewAdminDialog viewDialog) {
        this.adminPanel = adminPanel;
        this.addPanel = addPanel;
        this.editPanel = editPanel;
        this.viewDialog = viewDialog;

        setTableData();

    }

    @Override
    public void setTableData() {
        DefaultTableModel model = dao.fetchAll();
        adminPanel.jTable1.setModel(model);
        new SearchDefaultModel(adminPanel, adminPanel.jTable1, adminPanel.srchtxtfld, model);
    }

    @Override
    public void save() {
        if (addPanel.admin_id.getText().trim().equals("")
                || addPanel.adfname.getText().trim().equals("")
                || addPanel.admname.getText().trim().equals("")
                || addPanel.adlname.getText().trim().equals("")
                || addPanel.pstn.getText().trim().equals("")
                || addPanel.nmbr.getText().trim().equals("")
                || addPanel.ml.getText().trim().equals("")
                || addPanel.sx.getSelectedItem().equals("Sex")
                || addPanel.usrnm.getText().trim().equals("")
                || addPanel.cnfrm.getText().trim().equals("")
                || addPanel.psswrd.getText().trim().equals("")
                || addPanel.brgy.getText().trim().equals("")
                || addPanel.mncplty.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AdminModel admin = new AdminModel();
            if (dao.isUsernameTaken(admin.getUsername())) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }

            admin.setStaff_id(addPanel.admin_id.getText().trim());
            admin.setStFname(addPanel.adfname.getText().trim());
            admin.setStMname(addPanel.admname.getText().trim());
            admin.setStLname(addPanel.adlname.getText().trim());
//            admin.setPosition(addPanel.pstn.getText()); 
            admin.setConNum(addPanel.nmbr.getText().trim());
            admin.setEmail(addPanel.ml.getText().trim());
            admin.setSx(addPanel.sx.getSelectedItem().toString());
            admin.setBday(addPanel.bdy.getDate());
            admin.setUsername(addPanel.usrnm.getText().trim());
            admin.setPass(addPanel.cnfrm.getText().trim());
            admin.setImage(uploadedImageForAdd);
            admin.setBarangay(addPanel.brgy.getText().trim());
            admin.setMunicipal(addPanel.mncplty.getText().trim());
            admin.setCollge(addPanel.pstn.getText().trim());

            if (fingerprintTemplateAdd != null) {
                admin.setFingerprint(fingerprintTemplateAdd);
                System.out.println("Fingerprint Bytes: " + fingerprintTemplateAdd);
                byte[] imageBytes = ImageExtractor.extractImageBytes(addPanel.jLabelimage, "png", 120, 120);
                if (imageBytes != null) {
                    admin.setFingerprintImage(imageBytes);
//                    admin.setFingerprintImage(fingerprintImageAdd);

                }
                byte[] fingerBytes = ImageExtractor.extractImageBytes(addPanel.jLabelfinger, "png", 120, 120);
                if (fingerBytes != null) {
                    admin.setFingerprintImage(fingerBytes);
                    System.out.println("Fingerprint Image Bytes: " + fingerBytes);

                }
            } else {
                System.out.println("No fingerprint template captured.");
            }
            boolean saved = dao.save(admin);
            if (saved) {
                setTableData();
                clearAdd();
                JOptionPane.showMessageDialog(null, "Admin added successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Admin can't be added.");
            }

        }
    }

    @Override
    public void editView() {
        editPanel.admin_id.setText(viewDialog.adminID.getText().trim());
        editPanel.pstn.setText(viewDialog.college.getText().trim());
        editPanel.adfname.setText(viewDialog.fName.getText().trim());
        editPanel.admname.setText(viewDialog.mName.getText().trim());
        editPanel.adlname.setText(viewDialog.lName.getText().trim());
        editPanel.nmbr.setText(viewDialog.cntctNumber.getText().trim());
        editPanel.ml.setText(viewDialog.email.getText().trim());
        editPanel.sx.setSelectedItem(viewDialog.sex.getText());
        editPanel.brgy.setText(viewDialog.brgy.getText().trim());
        editPanel.mncplty.setText(viewDialog.municipal.getText().trim());
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = format.parse(viewDialog.bDay.getText());
            editPanel.bdy.setDate(parsedDate);
        } catch (ParseException ex) {
            Logger.getLogger(AdminSerImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        editPanel.usrnm.setText(viewDialog.usrName.getText().trim());

        if (viewDialog.image != null) {
            editPanel.jLabelimage.setText("");
            editPanel.jLabelimage.setIcon(viewDialog.image.getIcon());
        } else {
            editPanel.jLabelimage.setText("No Image");
        }
        if (viewDialog.fngrprnt != null) {
            editPanel.jLabelfinger.setText("");
            editPanel.jLabelfinger.setIcon(viewDialog.fngrprnt.getIcon());
        } else {
            editPanel.jLabelfinger.setText("No enrolled fingerprint");
        }

        CardLayout cl = (CardLayout) adminPanel.jPanel2.getLayout();
        adminPanel.jPanel2.add(editPanel, "EditAdmin");
        cl.show(adminPanel.jPanel2, "EditAdmin");
        viewDialog.dispose();
    }

    @Override
    public void update() {
        if (editPanel.admin_id.getText().trim().equals("")
                || editPanel.adfname.getText().trim().equals("")
                || editPanel.admname.getText().trim().equals("")
                || editPanel.adlname.getText().trim().equals("")
                || editPanel.pstn.getText().trim().equals("")
                || editPanel.nmbr.getText().trim().equals("")
                || editPanel.ml.getText().trim().equals("")
                || editPanel.sx.getSelectedItem().equals("Sex")
                || editPanel.usrnm.getText().trim().equals("")
                //                || editPanel.cnfrm.getText().trim().equals("")
                //                || editPanel.psswrd.getText().trim().equals("")
                || editPanel.brgy.getText().trim().equals("")
                || editPanel.mncplty.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AdminModel admin = new AdminModel();
            if (dao.isUsernameTaken(admin.getUsername())) {
                JOptionPane.showMessageDialog(null, "Username already exists. Please choose another.", "Warning", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int dataRow = adminPanel.jTable1.getSelectedRow();
            admin.setStaff_id((String) adminPanel.jTable1.getValueAt(dataRow, 0));
            admin.setStFname(editPanel.adfname.getText());
            admin.setStMname(editPanel.admname.getText());
            admin.setStLname(editPanel.adlname.getText());
            admin.setConNum(editPanel.nmbr.getText().trim());
            admin.setEmail(editPanel.ml.getText().trim());
            admin.setSx(editPanel.sx.getSelectedItem().toString());
            admin.setBday(editPanel.bdy.getDate());
            admin.setUsername(editPanel.usrnm.getText().trim());
            admin.setPass(editPanel.cnfrm.getText().trim());
            admin.setImage(uploadedImageForEdit);
            admin.setBarangay(editPanel.brgy.getText().trim());
            admin.setMunicipal(editPanel.mncplty.getText().trim());
            admin.setCollge(editPanel.pstn.getText().trim());

            if (editPanel.jLabelimage != null && editPanel.jLabelimage.getIcon() != null) {
                byte[] imageBytes = ImageExtractor.extractImageBytes(editPanel.jLabelimage, "jpg", 120, 120);
                if (imageBytes != null) {
                    admin.setImage(imageBytes);
                }

            }
            if (fingerprintTemplateEdit != null) {
                admin.setFingerprint(fingerprintTemplateEdit);
                admin.setFingerprintImage(fingerprintImageEdit);
            } else {
                System.out.println("No fingerprint template captured.");
            }

            boolean update = dao.update(admin);
            if (update) {
                setTableData();
                clearEdit();
                JOptionPane.showMessageDialog(null, "Admin Information updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Admin inforamtion can't be edited.");
            }

        }
    }

    @Override
    public void delete() {
        int dataRow = adminPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String staff_id = adminPanel.jTable1.getValueAt(dataRow, 0).toString();
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "Delete Admin: " + staff_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.delete(staff_id);
                setTableData();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select admin to delete.");
        }
    }

    @Override
    public void addButton() {
        CardLayout cl = (CardLayout) adminPanel.jPanel2.getLayout();
        adminPanel.jPanel2.add(addPanel, "AddAdmin");
        cl.show(adminPanel.jPanel2, "AddAdmin");
        System.out.println("AddButtonClicked");
    }

    @Override
    public void selectImageForAdd() {
        ImageUploader uploader = new ImageUploader();
        uploadedImageForAdd = uploader.pickImage(addPanel, addPanel.jLabelimage);
    }

    @Override
    public void selectImageForEdit() {
//        if (editPanel.jLabelimage != null) {
//            try {
//                BufferedImage img = new BufferedImage(editPanel.jLabelimage.getWidth(), editPanel.jLabelimage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(img, "png", baos);
//                uploadedImageForEdit = baos.toByteArray();
//            } catch (IOException ex) {
//                Logger.getLogger(AdminSerImpl.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        } else {
//            ImageUploader uploader = new ImageUploader();
//            uploadedImageForEdit = uploader.pickImage(editPanel, editPanel.jLabelimage);
//        }
        ImageUploader uploader = new ImageUploader();
        uploadedImageForEdit = uploader.pickImage(editPanel, editPanel.jLabelimage);
    }

    @Override
    public void scanFingerAdd() {
        addPanel.scn.setEnabled(false);

        try {
            String userIdToEnroll = addPanel.admin_id.getText();
            if (userIdToEnroll == null || userIdToEnroll.trim().isEmpty()) {
                JOptionPane.showMessageDialog(null, "Invalid user ID.");
                addPanel.scn.setEnabled(true);
                return;
            }
            userIdToEnroll = userIdToEnroll.trim();

            JDialog progressDialog = new JDialog((JFrame) null, "Scan Fingerprint", false); // Not modal
            progressDialog.setSize(300, 100);
            progressDialog.setLocationRelativeTo(null);
            progressDialog.setLayout(new BorderLayout());

            JProgressBar progressBar = new JProgressBar(0, 2);
            progressBar.setValue(0);
            progressBar.setString("Starting fingerprint enrollment...");
            progressBar.setStringPainted(true);
            progressDialog.add(progressBar, BorderLayout.CENTER);
            progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

            PromptSwing.promptProgressBar = progressBar;

            ReaderCollection readers;
            try {
                readers = UareUGlobal.GetReaderCollection();
                readers.GetReaders();

                if (readers.size() == 0) {
                    JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
                    addPanel.scn.setEnabled(true);
                    return;
                }
                if (readers.get(0) == null) {
                    JOptionPane.showMessageDialog(null, "Fingerprint reader object is null.");
                    addPanel.scn.setEnabled(true);
                    return;
                }

                Selection.reader = readers.get(0);
            } catch (UareUException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error initializing fingerprint reader: " + e.getMessage());
                addPanel.scn.setEnabled(true);
                return;
            }

            EnrollmentThread enrollmentThread = new EnrollmentThread(addPanel.jLabelfinger, progressBar, userIdToEnroll);
            enrollmentThread.start();

            SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));

            new Thread(() -> {
                try {
                    enrollmentThread.join();
                    FingerprintModel model = enrollmentThread.getEnrollUser();
                    if (model != null && model.getTemplate() != null) {
                        System.out.println("Fingerprint enrollment done!");
                        System.out.println("User ID: " + model.getUser_id());
                        System.out.println("FMD Length: " + model.getTemplate().length);
                        fingerprintTemplateAdd = model.getTemplate();
                    } else {
                        System.out.println("Enrollment failed or template is null.");
                    }
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }

                SwingUtilities.invokeLater(() -> {
                    System.out.println("Disposing progressDialog...");
                    progressDialog.dispose();
                    addPanel.scn.setEnabled(true);
                    addPanel.setEnabled(true);
                });
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid user ID.");
            addPanel.scn.setEnabled(true);
        }
    }

    @Override
    public void scanFingerEdit() {
        JDialog progressDialog = new JDialog((JFrame) null, "Scan Fingerprint", true);
        JProgressBar progressBar = new JProgressBar(0, 3); // 3 required scans
        progressBar.setValue(0);
        progressBar.setString("Scan 1 of 3");
        progressBar.setStringPainted(true);
        progressDialog.add(progressBar);
        progressDialog.setSize(300, 75);
        progressDialog.setLocationRelativeTo(null);

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
//                    fingerprintTemplateEdit = finalFmd.getData();
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
//                        ImageIcon icon = new ImageIcon(img.getScaledInstance(editPanel.jLabelfinger.getWidth(),
//                                editPanel.jLabelfinger.getHeight(), Image.SCALE_SMOOTH));
//                        SwingUtilities.invokeLater(() -> {
//                            editPanel.jLabelfinger.setText("");
//                            editPanel.jLabelfinger.setIcon(icon);
//                        });
//
//                        try {
//                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                            ImageIO.write(img, "png", baos);
//                            fingerprintImageEdit = baos.toByteArray();
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
//        worker.execute();
        progressDialog.setVisible(true);
    }

    @Override
    public void adminPopupMenu() {
        SwingUtilities.invokeLater(() -> {
            int rowAtPoint = adminPanel.jTable1.rowAtPoint(SwingUtilities.
                    convertPoint(adminPanel.adminPopUp, new Point(0, 0), adminPanel.jTable1));
            if (rowAtPoint > -1) {
                adminPanel.jTable1.setRowSelectionInterval(rowAtPoint, rowAtPoint);
            }
        });
    }

    @Override
    public void adminMouseEvent(MouseEvent e
    ) {
        int r = adminPanel.jTable1.rowAtPoint(e.getPoint());
        if (r >= 0 && r < adminPanel.jTable1.getRowCount()) {
            adminPanel.jTable1.setRowSelectionInterval(r, r);
        } else {
            adminPanel.jTable1.clearSelection();
        }

        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            adminPanel.adminPopUp.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    @Override
    public void viewAdmin() {
        int dataRow = adminPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String admin_id = getCellValue(dataRow, 0);

            JDialog progressDialog = new JDialog((JFrame) null, "Loading", true);
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setString("Fetching admin data...");
            progressBar.setStringPainted(true);
            progressDialog.add(progressBar);
            progressDialog.setSize(300, 75);
            progressDialog.setLocationRelativeTo(null);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    AdminModel admin = dao.view(admin_id);

                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();

                        viewDialog.adminID.setText(admin_id);
                        viewDialog.college.setText(getCellValue(dataRow, 1));
                        viewDialog.fName.setText(getCellValue(dataRow, 2));
                        viewDialog.mName.setText(getCellValue(dataRow, 3));
                        viewDialog.lName.setText(getCellValue(dataRow, 4));
                        viewDialog.cntctNumber.setText(getCellValue(dataRow, 5));
                        viewDialog.sex.setText(getCellValue(dataRow, 6));
                        viewDialog.bDay.setText(getCellValue(dataRow, 7));
                        viewDialog.brgy.setText(getCellValue(dataRow, 8));
                        viewDialog.municipal.setText(getCellValue(dataRow, 9));
                        viewDialog.email.setText(getCellValue(dataRow, 10));
                        viewDialog.usrName.setText(getCellValue(dataRow, 11));

                        if (admin.getImageData() != null) {
                            ImageIcon icon = new ImageIcon(admin.getImageData());
                            System.out.println("Icon class: " + (icon != null ? icon.getClass().getName() : "null"));

                            Image scaledImage = icon.getImage().getScaledInstance(
                                    viewDialog.image.getWidth(),
                                    viewDialog.image.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.image.setIcon(new ImageIcon(scaledImage));
                            viewDialog.image.setText("");
                        } else {
                            viewDialog.image.setIcon(null);
                            viewDialog.image.setText("No Image");
                        }

                        if (admin.getFngrprntImageData() != null) {
                            System.out.println("Byte array has img data. Size: " + admin.getFngrprntImageData().length + " bytes");
                            ImageIcon fingerprintIcon = new ImageIcon(admin.getFngrprntImageData());
                            System.out.println("Icon class: " + (fingerprintIcon != null ? fingerprintIcon.getClass().getName() : "null"));
                            Image scaledFingerprint = fingerprintIcon.getImage().getScaledInstance(
                                    viewDialog.fngrprnt.getWidth(),
                                    viewDialog.fngrprnt.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.fngrprnt.setIcon(new ImageIcon(scaledFingerprint));
                            viewDialog.fngrprnt.setText("");
                        } else {
                            viewDialog.fngrprnt.setIcon(null);
                            viewDialog.fngrprnt.setText("No fingerprint");
                        }

                        viewDialog.setLocationRelativeTo(null);
                        viewDialog.setVisible(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error fetching admin data: " + ex.getMessage());
                    });
                } finally {
                    executor.shutdown();
                }
            });

            progressDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select admin to update.");
        }
    }

    private String getCellValue(int row, int col) {
        Object val = adminPanel.jTable1.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    @Override
    public void clearAdd() {
        addPanel.admin_id.setText("");
        addPanel.pstn.setText("");
        addPanel.adfname.setText("");
        addPanel.admname.setText("");
        addPanel.adlname.setText("");
        addPanel.nmbr.setText("");
        addPanel.ml.setText("");
        addPanel.sx.setSelectedIndex(0);
        addPanel.bdy.setDate(null);
        addPanel.usrnm.setText("");
        addPanel.psswrd.setText("");
        addPanel.cnfrm.setText("");
        addPanel.jLabelimage.setText("");
        addPanel.jLabelimage.setIcon(null);
        addPanel.jLabelfinger.setText("");
        addPanel.jLabelfinger.setIcon(null);
        addPanel.brgy.setText("");
        addPanel.mncplty.setText("");
    }

    @Override
    public void clearEdit() {
        editPanel.admin_id.setText("");
        editPanel.pstn.setText("");
        editPanel.adfname.setText("");
        editPanel.admname.setText("");
        editPanel.adlname.setText("");
        editPanel.nmbr.setText("");
        editPanel.ml.setText("");
        editPanel.sx.setSelectedIndex(0);
        editPanel.bdy.setDate(null);
        editPanel.usrnm.setText("");
        editPanel.psswrd.setText("");
        editPanel.cnfrm.setText("");
        editPanel.jLabelimage.setText("");
        editPanel.jLabelimage.setIcon(null);
        editPanel.jLabelfinger.setText("");
        editPanel.jLabelfinger.setIcon(null);
        editPanel.brgy.setText("");
        editPanel.mncplty.setText("");
    }

}
