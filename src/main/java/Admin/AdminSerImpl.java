package Admin;

import Admin.Views.*;
import Utilities.ImageUploader;
import Utilities.QuickSearchList;
import Utilities.SearchDefaultModel;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
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
    private FingerprintCapture fingerprintCapture;
//    private DPFPTemplate fingerprintTemplate;

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

//            
//            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                admin.setFingerprint(template.serialize());
//
//                // Convert fingerprint sample to image byte[]
//                Image img = DPFPGlobal.getSampleConversionFactory().createImage(fingerprintCapture.getSample());  
//                BufferedImage bufferedImg = new BufferedImage(
//                        img.getWidth(null),
//                        img.getHeight(null),
//                        BufferedImage.TYPE_INT_RGB
//                );
//
//                Graphics2D g2 = bufferedImg.createGraphics();
//                g2.drawImage(img, 0, 0, null);
//                g2.dispose();
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(bufferedImg, "png", baos);
//                admin.setFingerprintImage(baos.toByteArray());
//            } else {
//                System.out.println("No fingerprint template captured.");
//            }
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
            editPanel.jLabelimage.setIcon(viewDialog.image.getIcon());
        } else {
            editPanel.jLabelimage.setText("No Image");
        }
        if (viewDialog.fngrprnt != null) {
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
                || editPanel.cnfrm.getText().trim().equals("")
                || editPanel.psswrd.getText().trim().equals("")
                || editPanel.brgy.getText().trim().equals("")
                || editPanel.mncplty.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AdminModel admin = new AdminModel();
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

//            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                admin.setFingerprint(template.serialize());
//
//                // Convert fingerprint sample to image byte[]
//                Image img = DPFPGlobal.getSampleConversionFactory().createImage(fingerprintCapture.getLastSample());  
//                BufferedImage bufferedImg = new BufferedImage(
//                        img.getWidth(null),
//                        img.getHeight(null),
//                        BufferedImage.TYPE_INT_RGB
//                );
//
//                Graphics2D g2 = bufferedImg.createGraphics();
//                g2.drawImage(img, 0, 0, null);
//                g2.dispose();
//
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                ImageIO.write(bufferedImg, "png", baos);
//                admin.setFingerprintImage(baos.toByteArray());
//            } else {
//                System.out.println("No fingerprint template captured.");
//            }
            boolean update = dao.update(admin);
            if (update) {
                setTableData();
                clearEdit();
                JOptionPane.showMessageDialog(null, "Admin Information updated successfully.");
            } else {
                JOptionPane.showMessageDialog(null, "Student is already in this class.");
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
        ImageUploader uploader = new ImageUploader();
        uploadedImageForEdit = uploader.pickImage(editPanel, editPanel.jLabelimage);
    }

    @Override
    public void scanFingerAdd() {
//        fingerprintCapture = new FingerprintCapture(addPanel.jLabelfinger); 
//        fingerprintCapture.startCapture();
//
//        JOptionPane.showMessageDialog(null, "Place your finger on the scanner.");
//
//        fingerprintTemplate = fingerprintCapture.getTemplate();  
//
//        if (fingerprintTemplate == null) {
//            JOptionPane.showMessageDialog(null, "Fingerprint not captured.");
//        } else {
//            JOptionPane.showMessageDialog(null, "Fingerprint captured successfully!");
//        }
//
//        fingerprintCapture.stopCapture(); 
    }

    @Override
    public void scanFingerEdit() {
//        fingerprintCapture = new FingerprintCapture(editPanel.jLabelfinger); 
//        fingerprintCapture.startCapture();
//
//        JOptionPane.showMessageDialog(null, "Place your finger on the scanner.");
//
//        fingerprintTemplate = fingerprintCapture.getTemplate();  
//
//        if (fingerprintTemplate == null) {
//            JOptionPane.showMessageDialog(null, "Fingerprint not captured.");
//        } else {
//            JOptionPane.showMessageDialog(null, "Fingerprint captured successfully!");
//        }
//
//        fingerprintCapture.stopCapture();
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
                            ImageIcon fingerprintIcon = new ImageIcon(admin.getFingerprintImage());
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
