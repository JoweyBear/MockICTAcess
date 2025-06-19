package Admin;

import Admin.Views.*;
import Utilities.FingerprintCapture;
import Utilities.ImageUploader;
import Utilities.QuickSearch;
import Utilities.QuickSearchList;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.DefaultCellEditor;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import net.proteanit.sql.DbUtils;

public class AdminSerImpl implements AdminService {

    AdminDAO dao = new AdminDAOImpl();
    AdminPanel adminPanel;
    AddAdPanel addPanel;
    EditAdPanel editPanel;
    private final ImageUploader imageUploader = new ImageUploader();
    private byte[] uploadedImageForAdd;
    private byte[] uploadedImageForEdit;
    private FingerprintCapture fingerprintCapture;
//    private DPFPTemplate fingerprintTemplate;

    public AdminSerImpl(AdminPanel adminPanel, AddAdPanel addPanel, EditAdPanel editPanel) {
        this.adminPanel = adminPanel;
        this.addPanel = addPanel;
        this.editPanel = editPanel;

    }

    @Override
    public void setTableData() {
        DefaultTableModel model = dao.fetchAll();
        adminPanel.jTable1.setModel(model);
//        new QuickSearchList(adminPanel, adminPanel.jTable1, adminPanel.srchtxtfld, model);
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
                || addPanel.psswrd.getText().trim().equals("")) {
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
            //            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                byte[] fingerprintData = template.serialize();
//                admin.setFingerprint(fingerprintData);
            System.out.println("Fingerprint saved successfully.");
//            } 
            //        } else {
            //            System.out.println("No fingerprint template captured.");
            //        }
            
            dao.save(admin);
            setTableData();
            addPanel.admin_id.setText("");
            addPanel.pstn.setText("");
            addPanel.adfname.setText("");
            addPanel.admname.setText("");
            addPanel.adlname.setText("");
            addPanel.nmbr.setText("");
            addPanel.ml.setText("");
            addPanel.sx.setSelectedIndex(0);
            addPanel.bdy.setDate(new java.util.Date());
            addPanel.usrnm.setText("");
            addPanel.psswrd.setText("");
            addPanel.cnfrm.setText("");
            addPanel.jLabelimage.setText("");
            addPanel.jLabelfinger.setText("");

        }
    }

    @Override
    public void editView() {
        int dataRow = adminPanel.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String admin_id = adminPanel.jTable1.getValueAt(dataRow, 0) + "";
            editPanel.admin_id.setText(admin_id);
            editPanel.adfname.setText((String) adminPanel.jTable1.getValueAt(dataRow, 1));
            editPanel.admname.setText((String) adminPanel.jTable1.getValueAt(dataRow, 2));
            editPanel.adlname.setText((String) adminPanel.jTable1.getValueAt(dataRow, 3));
            editPanel.pstn.setText((String) adminPanel.jTable1.getValueAt(dataRow, 4));
        } else {
            JOptionPane.showMessageDialog(null, "Please select admin to update.");
        }
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
                || editPanel.psswrd.getText().trim().equals("")) {
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
            //            DPFPTemplate template = fingerprintCapture.getTemplate();
//            if (template != null) {
//                byte[] fingerprintData = template.serialize();
//                admin.setFingerprint(fingerprintData);
            System.out.println("Fingerprint saved successfully.");
//            } 
            //        } else {
            //            System.out.println("No fingerprint template captured.");
            //        }
            
            dao.update(admin);
            setTableData();
            editPanel.admin_id.setText("");
            editPanel.pstn.setText("");
            editPanel.adfname.setText("");
            editPanel.admname.setText("");
            editPanel.adlname.setText("");
//            editPanel.jLabel3.setText("");

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
    public void scanFinger() {
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

}
