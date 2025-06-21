package Admin;

import Admin.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class AdminController {

    AdminPanel adminPanel;
    AddAdPanel addPanel;
    EditAdPanel editPanel;
    AdminService service;
    ViewAdminDialog viewDialog; 

    public AdminController(AdminPanel adminPanel, AddAdPanel addPanel, EditAdPanel editPanel, ViewAdminDialog viewDialog) {
        this.adminPanel = adminPanel;
        this.addPanel = addPanel;
        this.editPanel = editPanel;
        this.viewDialog = viewDialog;
        service = new AdminSerImpl(adminPanel, addPanel, editPanel, viewDialog);
        this.adminPanel.buttonListener(new ButtonEvent(), new PopUpEvent(), new PopUpEvent());
        this.viewDialog.buttonListener(new ButtonEvent());
        this.addPanel.buttonListener(new ButtonEvent());
        this.editPanel.buttonListener(new ButtonEvent());
    }

    class ButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == adminPanel.dd) {
                service.addButton();
//            } else if (e.getSource() == adminPanel.dt) {
//                service.editView();
//            } else if (e.getSource() == adminPanel.dlt) {
//                service.delete();
            } else if (e.getSource() == addPanel.scn) {
                service.scanFingerAdd();
            } else if (e.getSource() == addPanel.sv) {
                service.save();
            } else if (e.getSource() == editPanel.scn) {
                service.scanFingerEdit();
            } else if (e.getSource() == editPanel.pdt) {
                service.update();
            } else if (e.getSource() == addPanel.chssmg) {
                service.selectImageForAdd();
            } else if (e.getSource() == editPanel.chssmg) {
                service.selectImageForEdit();
            } else if (e.getSource() == adminPanel.deleteAdminMenu) {
                service.delete();
            } else if (e.getSource() == adminPanel.viewAdmin) {
                service.viewAdmin();
            }else if(e.getSource() == viewDialog.dt){
                service.editView();
            }

        }

    }

    class PopUpEvent extends MouseAdapter implements PopupMenuListener {

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
    }
}
