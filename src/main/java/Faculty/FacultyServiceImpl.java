package Faculty;

import Faculty.Views.*;
import Utilities.QuickSearchList;
import java.awt.Image;
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
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

public class FacultyServiceImpl implements FacultyService {

    AddFaPanel fAdd;
    EditFaPanel fEdit;
    FacultyPanel faPanel;
    ViewFacultyDialog viewDialog;
    FacultyDAO dao = new FacultyDAOImpl();

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
            String student_id = faPanel.jTable1.getValueAt(dataRow, 0).toString();

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
                    StudentModel student = dao.studentView(student_id);

                    SwingUtilities.invokeLater(() -> {
                        progressDialog.dispose();

                        viewDialog.studentID.setText(student_id);
                        viewDialog.college.setText(sPanel.jTable1.getValueAt(dataRow, 1).toString());
                        viewDialog.year.setText(sPanel.jTable1.getValueAt(dataRow, 2).toString());
                        viewDialog.section.setText(sPanel.jTable1.getValueAt(dataRow, 3).toString());
                        viewDialog.fName.setText(sPanel.jTable1.getValueAt(dataRow, 4).toString());
                        viewDialog.mName.setText(sPanel.jTable1.getValueAt(dataRow, 5).toString());
                        viewDialog.lName.setText(sPanel.jTable1.getValueAt(dataRow, 6).toString());
                        viewDialog.sex.setText(sPanel.jTable1.getValueAt(dataRow, 7).toString());
                        viewDialog.bDay.setText(sPanel.jTable1.getValueAt(dataRow, 8).toString());
                        viewDialog.cntctNumber.setText(sPanel.jTable1.getValueAt(dataRow, 9).toString());
                        viewDialog.email.setText(sPanel.jTable1.getValueAt(dataRow, 10).toString());
                        viewDialog.brgy.setText(sPanel.jTable1.getValueAt(dataRow, 11).toString());
                        viewDialog.municipal.setText(sPanel.jTable1.getValueAt(dataRow, 12).toString());

                        if (student.getImage() != null) {
                            ImageIcon icon = new ImageIcon(student.getImage());
                            Image scaledImage = icon.getImage().getScaledInstance(
                                    viewDialog.image.getWidth(),
                                    viewDialog.image.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.image.setIcon(new ImageIcon(scaledImage));
                        } else {
                            viewDialog.image.setText("No Image");
                        }

                        if (student.getFingerprintImage() != null) {
                            ImageIcon fingerprintIcon = new ImageIcon(student.getFingerprintImage());
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
                        JOptionPane.showMessageDialog(null, "Error fetching Student data: " + ex.getMessage());
                    });
                } finally {
                    executor.shutdown();
                }
            });

            progressDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(null, "Please select student to update.");
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
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void delete() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addButton() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void selectImageForAdd() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void selectImageForEdit() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void scanFingerAdd() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void scanFingerEdit() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void studentPopupMenu() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void studentMouseEvent(MouseEvent e) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
