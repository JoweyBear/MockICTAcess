package Admin;

import Admin.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AdminController {

    AdminPanel adminPanel;
    AddAdPanel addPanel;
    EditAdPanel editPanel;
    AdminService service;

    public AdminController(AdminPanel adminPanel, AddAdPanel addPanel, EditAdPanel editPanel) {
        this.adminPanel = adminPanel;
        this.addPanel = addPanel;
        this.editPanel = editPanel;
        service = new AdminSerImpl(adminPanel, addPanel, editPanel);
        this.adminPanel.buttonListener(new ButtonEvent());
        this.addPanel.buttonListener(new ButtonEvent());
        this.editPanel.buttonListener(new ButtonEvent());
    }

    class ButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == adminPanel.dd) {
                service.addButton();
            } else if (e.getSource() == adminPanel.dt) {
                service.editView();
            } else if (e.getSource() == adminPanel.dlt) {
                service.delete();
            } else if (e.getSource() == addPanel.scn) {
                service.scanFinger();
            } else if (e.getSource() == addPanel.sv) {
                service.save();
            } else if (e.getSource() == editPanel.scn) {
                service.scanFinger();
            } else if (e.getSource() == editPanel.pdt) {
                service.update();
            }else if(e.getSource() == addPanel.chssmg){
                service.selectImageForAdd();
            }else if(e.getSource() == editPanel.chssmg){
                service.selectImageForEdit();
            }

        }

    }
}
