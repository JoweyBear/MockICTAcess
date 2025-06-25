package Faculty;

import Faculty.Views.*;
import Utilities.FingerprintCapture;
import Utilities.ImageUploader;
import Utilities.QuickSearchList;
import java.awt.CardLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FacultyServiceImpl implements FacultyService {

    AddFaPanel fAdd;
    EditFaPanel fEdit;
    FacultyPanel faPanel;
    ViewFacultyDialog viewDialog;
    FacultyDAO dao = new FacultyDAOImpl();
    private final ImageUploader imageUploader = new ImageUploader();
    private byte[] uploadedImageForAdd;
    private byte[] uploadedImageForEdit;
    private FingerprintCapture fingerprintCapture;

    public FacultyServiceImpl(AddFaPanel fAdd, EditFaPanel fEdit, FacultyPanel faPanel, ViewFacultyDialog viewDialog) {
        this.fAdd = fAdd;
        this.fEdit = fEdit;
        this.faPanel = faPanel;
        this.viewDialog = viewDialog;
    }

    @Override
    public void setTableData() {
        DefaultTableModel model = dao.fetchAll();
        faPanel.jTable1.setModel(model);
        new QuickSearchList(faPanel, faPanel.jTable1, faPanel.srchtxtfld, (List<List<String>>) model);
    }

    @Override
    public void save() {
        if (fAdd.faculty_id.getText().trim().equals("")
                || fAdd.adfname.getText().trim().equals("")
                || fAdd.admname.getText().trim().equals("")
                || fAdd.adlname.getText().trim().equals("")
                || fAdd.nmbr.getText().trim().equals("")
                || fAdd.ml.getText().trim().equals("")
                || fAdd.sx.getSelectedItem().equals("Sex")
                || fAdd.cllg.getSelectedItem().equals("College")
                || fAdd.brgy.getText().trim().equals("")
                || fAdd.municipal.getText().trim().equals("")
                || fAdd.pstn.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            FacultyModel faculty = new FacultyModel();
            faculty.setFaculty_id(fAdd.faculty_id.getText().trim());
            faculty.setFname(fAdd.adfname.getText().trim());
            faculty.setMname(fAdd.admname.getText().trim());
            faculty.setLname(fAdd.adlname.getText().trim());
            faculty.setPosition(fAdd.pstn.getText());
            faculty.setCntctNmber(fAdd.nmbr.getText().trim());
            faculty.setEmail(fAdd.ml.getText().trim());
            faculty.setSx(fAdd.sx.getSelectedItem().toString());
            faculty.setBday((java.sql.Date) fAdd.bdy.getDate());
            faculty.setImage(uploadedImageForAdd);
            faculty.setCollege(fAdd.cllg.getSelectedItem().toString());

//            
//            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                faculty.setFingerprint(template.serialize());
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
//                faculty.setFingerprintImage(baos.toByteArray());
//            } else {
//                System.out.println("No fingerprint template captured.");
//            }
            dao.save(faculty);
            setTableData();
            clearAdd();
        }
    }

    @Override
    public void editView() {
        fEdit.faculty_id.setText(viewDialog.facultyID.getText().trim());
        fEdit.adfname.setText(viewDialog.fName.getText().trim());
        fEdit.admname.setText(viewDialog.mName.getText().trim());
        fEdit.adlname.setText(viewDialog.lName.getText().trim());
        fEdit.nmbr.setText(viewDialog.cntctNumber.getText().trim());
        fEdit.ml.setText(viewDialog.email.getText().trim());
        fEdit.sx.setSelectedItem(viewDialog.sex.getText());
        fEdit.cllg.setSelectedItem(viewDialog.college.getText());
        fEdit.jLabelimage.setIcon(viewDialog.image.getIcon());
        fEdit.jLabelfinger.setIcon(viewDialog.fngrprnt.getIcon());
        fEdit.brgy.setText(viewDialog.brgy.getText().trim());
        fEdit.municipal.setText(viewDialog.municipal.getText());
        fEdit.pstn.setText(viewDialog.pstn.getText().trim());
    }

    @Override
    public void viewStudent() {
        int dataRow = faPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String faculty_id = faPanel.jTable1.getValueAt(dataRow, 0).toString();

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
                    FacultyModel faculty = dao.facultyView(faculty_id);

                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();

                        viewDialog.facultyID.setText(faculty_id);
                        viewDialog.college.setText(faPanel.jTable1.getValueAt(dataRow, 1).toString());
                        viewDialog.pstn.setText(faPanel.jTable1.getValueAt(dataRow, 2).toString());
                        viewDialog.fName.setText(faPanel.jTable1.getValueAt(dataRow, 3).toString());
                        viewDialog.mName.setText(faPanel.jTable1.getValueAt(dataRow, 4).toString());
                        viewDialog.lName.setText(faPanel.jTable1.getValueAt(dataRow, 5).toString());
                        viewDialog.sex.setText(faPanel.jTable1.getValueAt(dataRow, 6).toString());
                        viewDialog.bDay.setText(faPanel.jTable1.getValueAt(dataRow, 7).toString());
                        viewDialog.cntctNumber.setText(faPanel.jTable1.getValueAt(dataRow, 8).toString());
                        viewDialog.email.setText(faPanel.jTable1.getValueAt(dataRow, 9).toString());
                        viewDialog.brgy.setText(faPanel.jTable1.getValueAt(dataRow, 10).toString());
                        viewDialog.municipal.setText(faPanel.jTable1.getValueAt(dataRow, 11).toString());

                        if (faculty.getImage() != null) {
                            ImageIcon icon = new ImageIcon(faculty.getImage());
                            Image scaledImage = icon.getImage().getScaledInstance(
                                    viewDialog.image.getWidth(),
                                    viewDialog.image.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.image.setIcon(new ImageIcon(scaledImage));
                        } else {
                            viewDialog.image.setText("No Image");
                        }

                        if (faculty.getFingerprintImage() != null) {
                            ImageIcon fingerprintIcon = new ImageIcon(faculty.getFingerprintImage());
                            Image scaledFingerprint = fingerprintIcon.getImage().getScaledInstance(
                                    viewDialog.fngrprnt.getWidth(),
                                    viewDialog.fngrprnt.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.fngrprnt.setIcon(new ImageIcon(scaledFingerprint));
                        } else {
                            viewDialog.fngrprnt.setText("No fingerprint");
                        }

                        viewDialog.setLocationRelativeTo(null);
                        viewDialog.setVisible(true);
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Error fetching Faculty data: " + ex.getMessage());
                    });
                } finally {
                    executor.shutdown();
                }
            });

            progressDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select faculty to update.");
        }

    }

    @Override
    public void clearAdd() {
        fAdd.faculty_id.setText("");
        fAdd.pstn.setText("");
        fAdd.adfname.setText("");
        fAdd.admname.setText("");
        fAdd.adlname.setText("");
        fAdd.nmbr.setText("");
        fAdd.ml.setText("");
        fAdd.sx.setSelectedIndex(-1);
        Date date = new Date();
        fAdd.bdy.setDate(date);
        fAdd.jLabelimage.setText("");
        fAdd.jLabelfinger.setText("");
        fAdd.brgy.setText("");
        fAdd.municipal.setText("");
    }

    @Override
    public void clearEdit() {
        fEdit.faculty_id.setText("");
        fEdit.pstn.setText("");
        fEdit.adfname.setText("");
        fEdit.admname.setText("");
        fEdit.adlname.setText("");
        fEdit.nmbr.setText("");
        fEdit.ml.setText("");
        fEdit.sx.setSelectedIndex(-1);
        Date date = new Date();
        fEdit.bdy.setDate(date);
        fEdit.jLabelimage.setText("");
        fEdit.jLabelfinger.setText("");
        fEdit.brgy.setText("");
        fEdit.municipal.setText("");
    }

    @Override
    public void update() {
        if (fEdit.faculty_id.getText().trim().equals("")
                || fEdit.adfname.getText().trim().equals("")
                || fEdit.admname.getText().trim().equals("")
                || fEdit.adlname.getText().trim().equals("")
                || fEdit.nmbr.getText().trim().equals("")
                || fEdit.ml.getText().trim().equals("")
                || fEdit.sx.getSelectedItem().equals("Sex")
                || fEdit.cllg.getSelectedItem().equals("College")
                || fEdit.pstn.getText().trim().equals("")
                || fEdit.brgy.getText().trim().equals("")
                || fEdit.municipal.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            FacultyModel faculty = new FacultyModel();
            int dataRow = faPanel.jTable1.getSelectedRow();
            faculty.setFaculty_id((String) faPanel.jTable1.getValueAt(dataRow, 0));
            faculty.setFname(fAdd.adfname.getText().trim());
            faculty.setMname(fAdd.admname.getText().trim());
            faculty.setLname(fAdd.adlname.getText().trim());
            faculty.setPosition(fAdd.pstn.getText());
            faculty.setCntctNmber(fAdd.nmbr.getText().trim());
            faculty.setEmail(fAdd.ml.getText().trim());
            faculty.setSx(fAdd.sx.getSelectedItem().toString());
            faculty.setBday((java.sql.Date) fAdd.bdy.getDate());
            faculty.setImage(uploadedImageForAdd);
            faculty.setCollege(fAdd.cllg.getSelectedItem().toString());

//            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                faculty.setFingerprint(template.serialize());
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
//                faculty.setFingerprintImage(baos.toByteArray());
//            } else {
//                System.out.println("No fingerprint template captured.");
//            }
            dao.update(faculty);
            setTableData();
            clearEdit();
        }
    }

    @Override
    public void delete() {
        int dataRow = faPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String staff_id = faPanel.jTable1.getValueAt(dataRow, 0).toString();
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "Delete faculty: " + staff_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.delete(staff_id);
                setTableData();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select faculty to delete.");
        }
    }

    @Override
    public void addButton() {
        CardLayout cl = (CardLayout) faPanel.jPanel2.getLayout();
        faPanel.jPanel2.add(fAdd, "AddAdmin");
        cl.show(faPanel.jPanel2, "AddAdmin");
        System.out.println("AddButtonClicked");
    }

    @Override
    public void selectImageForAdd() {
        ImageUploader uploader = new ImageUploader();
        uploadedImageForAdd = uploader.pickImage(fAdd, fAdd.jLabelimage);
    }

    @Override
    public void selectImageForEdit() {
        ImageUploader uploader = new ImageUploader();
        uploadedImageForEdit = uploader.pickImage(fEdit, fEdit.jLabelimage);
    }

    @Override
    public void scanFingerAdd() {
//        fingerprintCapture = new FingerprintCapture(fAdd.jLabelfinger); 
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
//        fingerprintCapture = new FingerprintCapture(fEdit.jLabelfinger); 
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
    public void facultyPopupMenu() {
        SwingUtilities.invokeLater(() -> {
            int rowAtPoint = faPanel.jTable1.rowAtPoint(SwingUtilities.
                    convertPoint(faPanel.facultyPopup, new Point(0, 0), faPanel.jTable1));
            if (rowAtPoint > -1) {
                faPanel.jTable1.setRowSelectionInterval(rowAtPoint, rowAtPoint);
            }
        });
    }

    @Override
    public void facultyMouseEvent(MouseEvent e) {
        int r = faPanel.jTable1.rowAtPoint(e.getPoint());
        if (r >= 0 && r < faPanel.jTable1.getRowCount()) {
            faPanel.jTable1.setRowSelectionInterval(r, r);
        } else {
            faPanel.jTable1.clearSelection();
        }

        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            faPanel.facultyPopup.show(e.getComponent(), e.getX(), e.getY());
        }    }
}
