package Student;

import Fingerprint.EnrollmentThread;
import Fingerprint.FingerprintModel;
import Fingerprint.PromptSwing;
import Fingerprint.Selection;
import Fingerprint.ThreadFlags;
import Student.Views.*;
import Utilities.GlobalVar;
import Utilities.ImageExtractor;
import Utilities.ImageUploader;
import Utilities.SearchDefaultModel;
import com.digitalpersona.uareu.ReaderCollection;
import com.digitalpersona.uareu.UareUException;
import com.digitalpersona.uareu.UareUGlobal;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class StudentServiceImpl implements StudentService {

    AddStudPanel sAdd;
    EditStudPanel sEdit;
    StudentPanel sPanel;
    ViewStudentDialog viewDialog;
    StudentDAO dao = new StudentDAOImpl();
    private final ImageUploader imageUploader = new ImageUploader();
    private byte[] uploadedImageForAdd;
    private byte[] uploadedImageForEdit;
    private byte[] fingerprintTemplateAdd;
    private byte[] fingerprintImageAdd;
    private byte[] fingerprintTemplateEdit;
    private byte[] fingerprintImageEdit;
    String collegeOfLoggedInAdmin = GlobalVar.loggedInAdmin.getCollge();

    public StudentServiceImpl(AddStudPanel sAdd, EditStudPanel sEdit, StudentPanel sPanel, ViewStudentDialog viewDialog) {
        this.sAdd = sAdd;
        this.sEdit = sEdit;
        this.sPanel = sPanel;
        this.viewDialog = viewDialog;

        setTableData();

    }

    @Override
    public void setTableData() {
        DefaultTableModel model = dao.fetchAll(collegeOfLoggedInAdmin);
        sPanel.jTable1.setModel(model);

        sPanel.jTable1.getColumn("Track").setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if ("N/A".equalsIgnoreCase(String.valueOf(value))) {
                    c.setForeground(Color.GRAY);
                    setToolTipText("This college does not require a track");
                } else {
                    c.setForeground(Color.BLACK);
                    setToolTipText(null);
                }

                return c;
            }
        });

        new SearchDefaultModel(sPanel, sPanel.jTable1, sPanel.srchtxtfld, model);
    }

    @Override
    public void save() {
        if (sAdd.student_id.getText().trim().equals("")
                || sAdd.adfname.getText().trim().equals("")
                || sAdd.admname.getText().trim().equals("")
                || sAdd.adlname.getText().trim().equals("")
                || sAdd.nmbr.getText().trim().equals("")
                || sAdd.ml.getText().trim().equals("")
                || sAdd.sx.getSelectedItem().equals("Sex")
                || sAdd.cllg.getSelectedItem().equals("College")
                || sAdd.yr.getSelectedItem().equals("Year")
                || sAdd.sctn.getSelectedItem().equals("Section")
                || sAdd.brgy.getText().trim().equals("")
                || sAdd.municipal.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            StudentModel student = new StudentModel();
            student.setStud_id(sAdd.student_id.getText().trim().toUpperCase());
            student.setFname(sAdd.adfname.getText().trim());
            student.setMname(sAdd.admname.getText().trim());
            student.setLname(sAdd.adlname.getText().trim());
//            admin.setPosition(addPanel.pstn.getText()); 
            student.setCntctNmber(sAdd.nmbr.getText().trim());
            student.setEmail(sAdd.ml.getText().trim());
            student.setSx(sAdd.sx.getSelectedItem().toString());
            student.setBday(sAdd.bdy.getDate());
            student.setBrgy(sAdd.brgy.getText().trim());
            student.setMunicipal(sAdd.municipal.getText().trim());
            student.setImage(uploadedImageForAdd);
            student.setSection(sAdd.sctn.getSelectedItem().toString());
            student.setYear(sAdd.yr.getSelectedItem().toString());

//            student.setCollege(sAdd.cllg.getSelectedItem().toString());
            String studentCollege = sAdd.cllg.getSelectedItem().toString();
            String track = sAdd.trck.getText().trim();

            if (studentCollege.equals("CICT")) {
                student.setCollege(studentCollege);
                student.setTrack(track);
            } else {
                student.setCollege(studentCollege);
                student.setTrack("N/A");
            }
            if (fingerprintTemplateAdd != null) {
                student.setFingerprint(fingerprintTemplateAdd);
                byte[] fingerBytes = ImageExtractor.extractImageBytes(sAdd.jLabelfinger, "jpg", 120, 120);
                student.setFingerprintImage(fingerBytes);
            } else {
                JOptionPane.showMessageDialog(null, "No fingerprint captured.");
            }

            int choice = JOptionPane.showConfirmDialog(null,
                    "Please check the information to be saved as ID can't be edited aftersaving.",
                    "Confirm Save", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                boolean save = dao.save(student);
                if (save) {
                    JOptionPane.showMessageDialog(null, "Student added successfully");
                    setTableData();
                    clearAdd();
                } else {
                    JOptionPane.showMessageDialog(null, "An error occured. Student can't be added.");
                }
            } else {
                System.out.println("Cancelled by Admin");
            }
        }
    }

    @Override
    public void editView() {
        sEdit.student_id.setText(viewDialog.studentID.getText().trim());
        sEdit.adfname.setText(viewDialog.fName.getText().trim());
        sEdit.admname.setText(viewDialog.mName.getText().trim());
        sEdit.adlname.setText(viewDialog.lName.getText().trim());
        sEdit.nmbr.setText(viewDialog.cntctNumber.getText().trim());
        sEdit.ml.setText(viewDialog.email.getText().trim());
        sEdit.sx.setSelectedItem(viewDialog.sex.getText());
        sEdit.cllg.setSelectedItem(viewDialog.college.getText());
        sEdit.yr.setSelectedItem(viewDialog.year.getText());
        sEdit.sctn.setSelectedItem(viewDialog.section.getText());
        sEdit.brgy.setText(viewDialog.brgy.getText().trim());
        sEdit.municipal.setText(viewDialog.municipal.getText());
        sEdit.trck.setText(viewDialog.track.getText());

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date parsedDate = format.parse(viewDialog.bDay.getText());
            sEdit.bdy.setDate(parsedDate);
        } catch (ParseException ex) {
            Logger.getLogger(StudentServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (viewDialog.image != null) {
            sEdit.jLabelimage.setText("");
            sEdit.jLabelimage.setIcon(viewDialog.image.getIcon());
        } else {
            sEdit.jLabelimage.setIcon(null);
            sEdit.jLabelimage.setText("No Image.");
        }
        if (viewDialog.fngrprnt != null) {
            sEdit.jLabelfinger.setText("");
            sEdit.jLabelfinger.setIcon(viewDialog.fngrprnt.getIcon());
        } else {
            sEdit.jLabelfinger.setIcon(null);
            sEdit.jLabelfinger.setText("No enrolled Fingerprint");
        }

        CardLayout cl = (CardLayout) sPanel.jPanel2.getLayout();
        sPanel.jPanel2.add(sEdit, "EditStudent");
        cl.show(sPanel.jPanel2, "EditStudent");
        viewDialog.dispose();
    }

    @Override
    public void viewStudent() {
        int dataRow = sPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String student_id = sPanel.jTable1.getValueAt(dataRow, 0).toString();

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
                        viewDialog.college.setText(getCellValue(dataRow, 1));
                        viewDialog.track.setText(getCellValue(dataRow, 2));
                        viewDialog.section.setText(getCellValue(dataRow, 3));
                        viewDialog.year.setText(getCellValue(dataRow, 4));
                        viewDialog.fName.setText(getCellValue(dataRow, 5));
                        viewDialog.mName.setText(getCellValue(dataRow, 6));
                        viewDialog.lName.setText(getCellValue(dataRow, 7));
                        viewDialog.sex.setText(getCellValue(dataRow, 8));
                        viewDialog.bDay.setText(getCellValue(dataRow, 9));
                        viewDialog.cntctNumber.setText(getCellValue(dataRow, 10));
                        viewDialog.email.setText(getCellValue(dataRow, 11));
                        viewDialog.brgy.setText(getCellValue(dataRow, 12));
                        viewDialog.municipal.setText(getCellValue(dataRow, 13));

                        if (student.getImageData() != null) {
                            ImageIcon icon = new ImageIcon(student.getImageData());
                            Image scaledImage = icon.getImage().getScaledInstance(
                                    viewDialog.image.getWidth(),
                                    viewDialog.image.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.image.setIcon(new ImageIcon(scaledImage));
                            viewDialog.image.setText("");
                        } else {
                            viewDialog.image.setText("No Image");
                            viewDialog.image.setIcon(null);
                        }

                        if (student.getFingerprintImageData() != null) {
                            ImageIcon fingerprintIcon = new ImageIcon(student.getFingerprintImageData());
                            Image scaledFingerprint = fingerprintIcon.getImage().getScaledInstance(
                                    viewDialog.fngrprnt.getWidth(),
                                    viewDialog.fngrprnt.getHeight(),
                                    Image.SCALE_SMOOTH
                            );
                            viewDialog.fngrprnt.setIcon(new ImageIcon(scaledFingerprint));
                            viewDialog.fngrprnt.setText("");
                        } else {
                            viewDialog.fngrprnt.setText("No fingerprint");
                            viewDialog.fngrprnt.setIcon(null);
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

    private String getCellValue(int row, int col) {
        Object val = sPanel.jTable1.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    @Override
    public void clearAdd() {
        sAdd.student_id.setText("");
        sAdd.adfname.setText("");
        sAdd.admname.setText("");
        sAdd.adlname.setText("");
        sAdd.nmbr.setText("");
        sAdd.ml.setText("");
        sAdd.sx.setSelectedIndex(0);
        sAdd.cllg.setSelectedIndex(0);
        sAdd.yr.setSelectedIndex(0);
        sAdd.bdy.setDate(null);
        sAdd.sctn.setSelectedIndex(0);
        sAdd.jLabelimage.setIcon(null);
        sAdd.jLabelfinger.setIcon(null);
        sAdd.jLabelimage.setText("");
        sAdd.jLabelfinger.setText("");
        sAdd.brgy.setText("");
        sAdd.municipal.setText("");
        sAdd.trck.setText("");
    }

    @Override
    public void clearEdit() {
        sEdit.student_id.setText("");
        sEdit.adfname.setText("");
        sEdit.admname.setText("");
        sEdit.adlname.setText("");
        sEdit.nmbr.setText("");
        sEdit.ml.setText("");
        sEdit.sx.setSelectedIndex(0);
        sEdit.bdy.setDate(null);
        sEdit.cllg.setSelectedIndex(0);
        sEdit.yr.setSelectedIndex(0);
        sEdit.sctn.setSelectedIndex(0);
        sEdit.jLabelimage.setIcon(null);
        sEdit.jLabelfinger.setIcon(null);
        sEdit.jLabelimage.setText("");
        sEdit.jLabelfinger.setText("");
        sEdit.brgy.setText("");
        sEdit.municipal.setText("");
        sEdit.trck.setText("");
    }

    @Override
    public void update() {
        if (sEdit.student_id.getText().trim().equals("")
                || sEdit.adfname.getText().trim().equals("")
                || sEdit.admname.getText().trim().equals("")
                || sEdit.adlname.getText().trim().equals("")
                || sEdit.nmbr.getText().trim().equals("")
                || sEdit.ml.getText().trim().equals("")
                || sEdit.sx.getSelectedItem().equals("Sex")
                || sEdit.cllg.getSelectedItem().equals("College")
                || sEdit.yr.getSelectedItem().equals("Year")
                || sEdit.sctn.getSelectedItem().equals("Section")
                || sEdit.brgy.getText().trim().equals("")
                || sEdit.municipal.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            StudentModel student = new StudentModel();
            int dataRow = sPanel.jTable1.getSelectedRow();
            student.setStud_id(sPanel.jTable1.getValueAt(dataRow, 0).toString().toUpperCase());
            student.setFname(sEdit.adfname.getText().trim());
            student.setMname(sEdit.admname.getText().trim());
            student.setLname(sEdit.adlname.getText().trim());
//            admin.setPosition(addPanel.pstn.getText()); 
            student.setCntctNmber(sEdit.nmbr.getText().trim());
            student.setEmail(sEdit.ml.getText().trim());
            student.setSx(sEdit.sx.getSelectedItem().toString());
            student.setBday(sEdit.bdy.getDate());
            student.setBrgy(sEdit.brgy.getText().trim());
            student.setMunicipal(sEdit.municipal.getText().trim());
            student.setImage(uploadedImageForEdit);
            student.setSection(sEdit.sctn.getSelectedItem().toString());
            student.setYear(sEdit.yr.getSelectedItem().toString());
//            student.setCollege(sAdd.cllg.getSelectedItem().toString());
            String studentCollege = sEdit.cllg.getSelectedItem().toString();
            String track = sEdit.trck.getText().trim();
            student.setCollege(studentCollege);
            if (studentCollege.equals("CICT")) {
                student.setTrack(track);
            } else {
//                student.setCollege(studentCollege);
                student.setTrack("N/A");
            }

//            byte[] imageBytes = ImageExtractor.extractImageBytes(sEdit.jLabelimage, "jpg", 120, 120);
//            if (imageBytes != null) {
//                student.setImage(imageBytes);
//            }
            if (sEdit.jLabelimage != null && sEdit.jLabelimage.getIcon() != null) {
                byte[] imageBytes = ImageExtractor.extractImageBytes(sEdit.jLabelimage, "jpg", 120, 120);
                if (imageBytes != null) {
                    student.setImage(imageBytes);
                }
            } else {
                student.setImage(uploadedImageForEdit);
            }

            if (fingerprintTemplateEdit != null) {
                student.setFingerprint(fingerprintTemplateEdit);
                byte[] fingerBytes = ImageExtractor.extractImageBytes(sEdit.jLabelfinger, "jpg", 120, 120);
                student.setFingerprintImage(fingerBytes);
            } else {
                JOptionPane.showMessageDialog(null, "No fingerprint captured.");
            }

            int choice = JOptionPane.showConfirmDialog(null,
                    "Please check the information to be updated.",
                    "Confirm Save", JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
            if (choice == JOptionPane.YES_OPTION) {
                boolean updated = dao.update(student);
                if (updated) {
                    setTableData();
                    clearEdit();
                    JOptionPane.showMessageDialog(null, "Student information updated successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "An error occured. Student can't be added.");
                }
            } else {
                System.out.println("Cancelled by Admin");
            }
        }
    }

    @Override
    public void delete() {
        int dataRow = sPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String student_id = sPanel.jTable1.getValueAt(dataRow, 0).toString();
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "Delete Student: " + student_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.delete(student_id);
                setTableData();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select student to delete.");
        }
    }

    @Override
    public void selectImageForAdd() {
        ImageUploader uploader = new ImageUploader();
        uploadedImageForAdd = uploader.pickImage(sAdd, sAdd.jLabelimage);
    }

    @Override
    public void selectImageForEdit() {
        ImageUploader uploader = new ImageUploader();
        uploadedImageForEdit = uploader.pickImage(sEdit, sEdit.jLabelimage);
    }

    @Override
    public void scanFingerAdd() {
        sAdd.scn.setEnabled(false);

        String userIdToEnroll = sAdd.student_id.getText();
        if (userIdToEnroll == null || userIdToEnroll.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid user ID.");
            sAdd.scn.setEnabled(true);
            return;
        }
        userIdToEnroll = userIdToEnroll.trim();

        // Setup progress dialog
        JDialog progressDialog = new JDialog((JFrame) null, "Scan Fingerprint", false);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null);
        progressDialog.setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 4); // Assuming 4 required scans
        progressBar.setValue(0);
        progressBar.setString("Starting fingerprint enrollment...");
        progressBar.setStringPainted(true);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        PromptSwing.promptProgressBar = progressBar;

        // Initialize reader
        ReaderCollection readers;
        try {
            readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();

            if (readers.size() == 0 || readers.get(0) == null) {
                JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
                sAdd.scn.setEnabled(true);
                return;
            }

            Selection.reader = readers.get(0);
        } catch (UareUException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing fingerprint reader: " + e.getMessage());
            sAdd.scn.setEnabled(true);
            return;
        }

        // Reset thread flags before starting
        ThreadFlags.running = true;
        EnrollmentThread enrollmentThread = new EnrollmentThread(sAdd.jLabelfinger, progressBar, userIdToEnroll);

        // Handle window close to cancel enrollment
        progressDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window close requested — stopping enrollment.");
                enrollmentThread.requestStop();
                enrollmentThread.stopEnrollmentThread();
                progressDialog.dispose();
            }
        });

        // Start enrollment thread
        enrollmentThread.start();

        // Show progress dialog
        SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));

        // Monitor enrollment completion
        new Thread(() -> {
            try {
                enrollmentThread.join();
                FingerprintModel model = enrollmentThread.getEnrollUser();

                if (model != null && model.getTemplate() != null) {
                    System.out.println("Fingerprint enrollment done!");
                    System.out.println("User ID: " + model.getUser_id());
                    System.out.println("FMD Length: " + model.getTemplate().length);
                    fingerprintTemplateEdit = model.getTemplate();
                } else {
                    System.out.println("Enrollment failed or template is null.");
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Disposing progressDialog...");
                    progressDialog.dispose();
                    sAdd.scn.setEnabled(true);
                    sAdd.setEnabled(true);
                });
            }
        }).start();
    }

    @Override
    public void scanFingerEdit() {
        sEdit.scn.setEnabled(false);

        String userIdToEnroll = sEdit.student_id.getText();
        if (userIdToEnroll == null || userIdToEnroll.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, "Invalid user ID.");
            sEdit.scn.setEnabled(true);
            return;
        }
        userIdToEnroll = userIdToEnroll.trim();

        // Setup progress dialog
        JDialog progressDialog = new JDialog((JFrame) null, "Scan Fingerprint", false);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null);
        progressDialog.setLayout(new BorderLayout());

        JProgressBar progressBar = new JProgressBar(0, 4); // Assuming 4 required scans
        progressBar.setValue(0);
        progressBar.setString("Starting fingerprint enrollment...");
        progressBar.setStringPainted(true);
        progressDialog.add(progressBar, BorderLayout.CENTER);
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);

        PromptSwing.promptProgressBar = progressBar;

        // Initialize reader
        ReaderCollection readers;
        try {
            readers = UareUGlobal.GetReaderCollection();
            readers.GetReaders();

            if (readers.size() == 0 || readers.get(0) == null) {
                JOptionPane.showMessageDialog(null, "No fingerprint reader found.");
                sEdit.scn.setEnabled(true);
                return;
            }

            Selection.reader = readers.get(0);
        } catch (UareUException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error initializing fingerprint reader: " + e.getMessage());
            sEdit.scn.setEnabled(true);
            return;
        }

        // Reset thread flags before starting
        ThreadFlags.running = true;
        EnrollmentThread enrollmentThread = new EnrollmentThread(sEdit.jLabelfinger, progressBar, userIdToEnroll);

        // Handle window close to cancel enrollment
        progressDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Window close requested — stopping enrollment.");
                enrollmentThread.requestStop();
                enrollmentThread.stopEnrollmentThread();
                progressDialog.dispose();
            }
        });

        // Start enrollment thread
        enrollmentThread.start();

        // Show progress dialog
        SwingUtilities.invokeLater(() -> progressDialog.setVisible(true));

        // Monitor enrollment completion
        new Thread(() -> {
            try {
                enrollmentThread.join();
                FingerprintModel model = enrollmentThread.getEnrollUser();

                if (model != null && model.getTemplate() != null) {
                    System.out.println("Fingerprint enrollment done!");
                    System.out.println("User ID: " + model.getUser_id());
                    System.out.println("FMD Length: " + model.getTemplate().length);
                    fingerprintTemplateEdit = model.getTemplate();
                } else {
                    System.out.println("Enrollment failed or template is null.");
                }
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            } finally {
                SwingUtilities.invokeLater(() -> {
                    System.out.println("Disposing progressDialog...");
                    progressDialog.dispose();
                    sEdit.scn.setEnabled(true);
                    sEdit.setEnabled(true);
                });
            }
        }).start();
    }

    @Override
    public void studentPopupMenu() {
        SwingUtilities.invokeLater(() -> {
            int rowAtPoint = sPanel.jTable1.rowAtPoint(SwingUtilities.
                    convertPoint(sPanel.studentPopup, new Point(0, 0), sPanel.jTable1));
            if (rowAtPoint > -1) {
                sPanel.jTable1.setRowSelectionInterval(rowAtPoint, rowAtPoint);
            }
        });
    }

    @Override
    public void studentMouseEvent(MouseEvent e) {
        int r = sPanel.jTable1.rowAtPoint(e.getPoint());
        if (r >= 0 && r < sPanel.jTable1.getRowCount()) {
            sPanel.jTable1.setRowSelectionInterval(r, r);
        } else {
            sPanel.jTable1.clearSelection();
        }

        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            sPanel.studentPopup.show(e.getComponent(), e.getX(), e.getY());
        }

    }

    @Override
    public void addButton() {
        CardLayout cl = (CardLayout) sPanel.jPanel2.getLayout();
        sPanel.jPanel2.add(sAdd, "AddStudent");
        cl.show(sPanel.jPanel2, "AddStudent");
        System.out.println("AddButtonClicked");
    }

}
