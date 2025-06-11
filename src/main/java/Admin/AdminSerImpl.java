package Admin;

import Admin.Views.*;
import Utilities.QuickSearch;
import Utilities.QuickSearchList;
import java.awt.CardLayout;
import java.awt.Dialog;
import java.sql.ResultSet;
import java.util.Arrays;
import javax.swing.DefaultCellEditor;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;
import net.proteanit.sql.DbUtils;

public class AdminSerImpl implements AdminService {

    AdminDAO dao = new AdminDAOImpl();
    AdminPanel adminPanel;
    AddPanel addPanel;
    EditPanel editPanel;

    public AdminSerImpl(AdminPanel adminPanel, AddPanel addPanel, EditPanel editPanel) {
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
                || addPanel.pstn.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AdminModel admin = new AdminModel();
            admin.setStaff_id(addPanel.admin_id.getText());
            admin.setStFname(addPanel.adfname.getText());
            admin.setStMname(addPanel.admname.getText());
            admin.setStLname(addPanel.adlname.getText());
//            admin.setPosition(addPanel.pstn.getText());
            dao.save(admin);
            setTableData();
            addPanel.admin_id.setText("");
            addPanel.pstn.setText("");
            addPanel.adfname.setText("");
            addPanel.admname.setText("");
            addPanel.adlname.setText("");
            addPanel.jLabelimage.setText("");

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
        if ((editPanel.admin_id.getText().trim().equals("")
                || editPanel.adfname.getText().trim().equals("")
                || editPanel.admname.getText().trim().equals("")
                || editPanel.adlname.getText().trim().equals("")
                || editPanel.pstn.getText().trim().equals(""))) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AdminModel admin = new AdminModel();
            int dataRow = adminPanel.jTable1.getSelectedRow();
            admin.setStaff_id((String) adminPanel.jTable1.getValueAt(dataRow, 0));
            admin.setStFname(editPanel.adfname.getText());
            admin.setStMname(editPanel.admname.getText());
            admin.setStLname(editPanel.adlname.getText());
//            admin.setPosition(editPanel.pstn.getText());
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
    public void selectImage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void scanFinger() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
