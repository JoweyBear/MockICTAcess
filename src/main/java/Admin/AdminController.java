package Admin;

import Admin.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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

        this.addPanel.nmbr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String number = addPanel.nmbr.getText();

                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }

                if (number.length() >= 11) {
                    e.consume();
                    return;
                }

                if (number.length() == 0 && c != '0') {
                    e.consume();
                } else if (number.length() == 1 && c != '9') {
                    e.consume();
                }
            }
        });
        this.editPanel.nmbr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String number = editPanel.nmbr.getText();

                if (!Character.isDigit(c)) {
                    e.consume();
                    return;
                }

                if (number.length() >= 11) {
                    e.consume();
                    return;
                }

                if (number.length() == 0 && c != '0') {
                    e.consume();
                } else if (number.length() == 1 && c != '9') {
                    e.consume();
                }
            }
        });
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
        } else if (e.getSource() == viewDialog.dt) {
            service.editView();
        }

    }

}

class PopUpEvent extends MouseAdapter implements PopupMenuListener {

    @Override
    public void mouseReleased(MouseEvent e) {
        service.adminMouseEvent(e);
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        service.adminPopupMenu();
    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
    }
}
}
