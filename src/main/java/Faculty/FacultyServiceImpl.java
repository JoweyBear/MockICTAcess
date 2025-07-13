package Faculty;

import Faculty.Views.*;
import Utilities.ImageUploader;
import Utilities.SearchDefaultModel;
import com.digitalpersona.uareu.Fmd;
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
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import utilities.FingerprintCapture;

public class FacultyServiceImpl implements FacultyService {

    AddFaPanel fAdd;
    EditFaPanel fEdit;
    FacultyPanel faPanel;
    ViewFacultyDialog viewDialog;
    FacultyDAO dao = new FacultyDAOImpl();
    private final ImageUploader imageUploader = new ImageUploader();
    private byte[] uploadedImageForAdd;
    private byte[] uploadedImageForEdit;
    private byte[] fingerprintTemplateAdd;
    private byte[] fingerprintImageAdd;
    private byte[] fingerprintTemplateEdit;
    private byte[] fingerprintImageEdit;
    FingerprintCapture scanner;

    public FacultyServiceImpl(AddFaPanel fAdd, EditFaPanel fEdit, FacultyPanel faPanel, ViewFacultyDialog viewDialog) {
        this.fAdd = fAdd;
        this.fEdit = fEdit;
        this.faPanel = faPanel;
        this.viewDialog = viewDialog;

        setTableData();
    }

    @Override
    public void setTableData() {
        DefaultTableModel model = dao.fetchAll();
        faPanel.jTable1.setModel(model);
        new SearchDefaultModel(faPanel, faPanel.jTable1, faPanel.srchtxtfld, model);
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
            faculty.setBday(fAdd.bdy.getDate());
            faculty.setImage(uploadedImageForAdd);
            faculty.setCollege(fAdd.cllg.getSelectedItem().toString());
            faculty.setBrgy(fAdd.brgy.getText().trim());
            faculty.setMunicipal(fAdd.municipal.getText().trim());

            if (fingerprintTemplateAdd != null) {
                faculty.setFingerprint(fingerprintTemplateAdd);
                faculty.setFingerprintImage(fingerprintImageAdd);
            } else {
                System.out.println("No fingerprint template captured.");
            }
            boolean save = dao.save(faculty);
            if (save) {
                JOptionPane.showMessageDialog(null, "Faculty added succesfully");
                setTableData();
                clearAdd();
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Faculty can't be added.");
            }

        }
    }

    @Override
    public void editView() {
        fEdit.faculty_id.setText(viewDialog.facultyID.getText().trim());
        fEdit.pstn.setText(viewDialog.pstn.getText().trim());
        fEdit.adfname.setText(viewDialog.fName.getText().trim());
        fEdit.admname.setText(viewDialog.mName.getText().trim());
        fEdit.adlname.setText(viewDialog.lName.getText().trim());
        fEdit.nmbr.setText(viewDialog.cntctNumber.getText().trim());
        fEdit.ml.setText(viewDialog.email.getText().trim());
        fEdit.sx.setSelectedItem(viewDialog.sex.getText());
        fEdit.cllg.setSelectedItem(viewDialog.college.getText());
        fEdit.brgy.setText(viewDialog.brgy.getText().trim());
        fEdit.municipal.setText(viewDialog.municipal.getText());
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = format.parse(viewDialog.bDay.getText());
            fEdit.bdy.setDate(parsedDate);
        } catch (ParseException ex) {
            Logger.getLogger(FacultyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (viewDialog.image != null) {
            fEdit.jLabelimage.setIcon(viewDialog.image.getIcon());
            fEdit.jLabelimage.setText("");
        } else {
            fEdit.jLabelimage.setText("No Image");
        }
        if (viewDialog.fngrprnt != null) {
            fEdit.jLabelfinger.setIcon(viewDialog.fngrprnt.getIcon());
            fEdit.jLabelfinger.setText("");
        } else {
            fEdit.jLabelfinger.setText("No enrolled fingerprint");
        }

        CardLayout cl = (CardLayout) faPanel.jPanel2.getLayout();
        faPanel.jPanel2.add(fEdit, "EditFaculty");
        cl.show(faPanel.jPanel2, "EditFaculty");
        viewDialog.dispose();
    }

    @Override
    public void viewFaculty() {
        int dataRow = faPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String faculty_id = faPanel.jTable1.getValueAt(dataRow, 0).toString();

            JDialog progressDialog = new JDialog((JFrame) null, "Loading", true);
            JProgressBar progressBar = new JProgressBar();
            progressBar.setIndeterminate(true);
            progressBar.setString("Fetching faculty data...");
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
                        viewDialog.college.setText(getCellValue(dataRow, 1));
                        viewDialog.pstn.setText(getCellValue(dataRow, 2));
                        viewDialog.fName.setText(getCellValue(dataRow, 3));
                        viewDialog.mName.setText(getCellValue(dataRow, 4));
                        viewDialog.lName.setText(getCellValue(dataRow, 5));
                        viewDialog.sex.setText(getCellValue(dataRow, 7));
                        viewDialog.bDay.setText(getCellValue(dataRow, 8));
                        viewDialog.cntctNumber.setText(getCellValue(dataRow, 6));
                        viewDialog.email.setText(getCellValue(dataRow, 11));
                        viewDialog.brgy.setText(getCellValue(dataRow, 9));
                        viewDialog.municipal.setText(getCellValue(dataRow, 10));

                        if (faculty.getImageData() != null) {
                            ImageIcon icon = new ImageIcon(faculty.getImageData());
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

                        if (faculty.getFingerprintImageData() != null) {
                            ImageIcon fingerprintIcon = new ImageIcon(faculty.getFingerprintImageData());
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

    private String getCellValue(int row, int col) {
        Object val = faPanel.jTable1.getValueAt(row, col);
        return val != null ? val.toString() : "";
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
        fAdd.bdy.setDate(null);
        fAdd.jLabelfinger.setIcon(null);
        fAdd.jLabelimage.setIcon(null);
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
        fEdit.jLabelfinger.setIcon(null);
        fEdit.jLabelimage.setIcon(null);
        fEdit.bdy.setDate(null);
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
            faculty.setFname(fEdit.adfname.getText().trim());
            faculty.setMname(fEdit.admname.getText().trim());
            faculty.setLname(fEdit.adlname.getText().trim());
            faculty.setPosition(fEdit.pstn.getText());
            faculty.setCntctNmber(fEdit.nmbr.getText().trim());
            faculty.setEmail(fEdit.ml.getText().trim());
            faculty.setSx(fEdit.sx.getSelectedItem().toString());
            faculty.setBday(fEdit.bdy.getDate());
            faculty.setImage(uploadedImageForEdit);
            faculty.setCollege(fEdit.cllg.getSelectedItem().toString());
            faculty.setBrgy(fEdit.brgy.getText().trim());
            faculty.setMunicipal(fEdit.municipal.getText().trim());
            if (fingerprintTemplateEdit != null) {
                faculty.setFingerprint(fingerprintTemplateEdit);
                faculty.setFingerprintImage(fingerprintImageEdit);
            } else {
                System.out.println("No fingerprint template captured");
            }
            boolean update = dao.update(faculty);
            if (update) {
                setTableData();
                clearEdit();
                JOptionPane.showMessageDialog(null, "Faculty successfully updated.");
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Faculty can't be updated");
            }
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
        faPanel.jPanel2.add(fAdd, "AddFaculty");
        cl.show(faPanel.jPanel2, "AddFaculty");
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
        scanner = new FingerprintCapture(fAdd.jLabelfinger);

        if (scanner.initializeReader()) {
            if (scanner.captureFingerprint()) {

                Fmd fmd = scanner.getCapturedFmd();
                if (fingerprintTemplateAdd == null) {
                    JOptionPane.showMessageDialog(null, "Fingerprint not captured.");
                }
                fingerprintTemplateAdd = fmd.getData();
                try {
                    ImageIcon icon = (ImageIcon) fAdd.jLabelfinger.getIcon();
                    Image image = icon.getImage();
                    BufferedImage bufferedImage = new BufferedImage(
                            image.getWidth(null),
                            image.getHeight(null),
                            BufferedImage.TYPE_INT_RGB
                    );

                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    ImageIO.write(bufferedImage, "png", baos);
                    fingerprintImageAdd = baos.toByteArray();
                } catch (IOException ex) {
                    Logger.getLogger(FacultyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fingerprint captured successfully!");
            }
        }
        scanner.closeReader();
    }

    @Override
    public void scanFingerEdit() {
        scanner = new FingerprintCapture(fEdit.jLabelfinger);

        if (scanner.initializeReader()) {
            if (scanner.captureFingerprint()) {

                Fmd fmd = scanner.getCapturedFmd();
                if (fingerprintTemplateEdit == null) {
                    JOptionPane.showMessageDialog(null, "Fingerprint not captured.");
                }
                fingerprintTemplateEdit = fmd.getData();
                try {
                    ImageIcon icon = (ImageIcon) fEdit.jLabelfinger.getIcon();
                    Image image = icon.getImage();
                    BufferedImage bufferedImage = new BufferedImage(
                            image.getWidth(null),
                            image.getHeight(null),
                            BufferedImage.TYPE_INT_RGB
                    );

                    Graphics2D g2d = bufferedImage.createGraphics();
                    g2d.drawImage(image, 0, 0, null);
                    g2d.dispose();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    ImageIO.write(bufferedImage, "png", baos);
                    fingerprintImageEdit = baos.toByteArray();
                } catch (IOException ex) {
                    Logger.getLogger(FacultyServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fingerprint captured successfully!");
            }
        }
        scanner.closeReader();
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
        }
    }
}
