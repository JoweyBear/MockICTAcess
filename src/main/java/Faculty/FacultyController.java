package Faculty;

import Faculty.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class FacultyController {

    AddFaPanel addPanel;
    EditFaPanel editPanel;
    FacultyPanel faPanel;
    ViewFacultyDialog viewDialog;
    FacultyService service;

    public FacultyController(AddFaPanel addPanel, EditFaPanel editPanel, FacultyPanel faPanel, ViewFacultyDialog viewDialog) {
        this.addPanel = addPanel;
        this.editPanel = editPanel;
        this.faPanel = faPanel;
        this.viewDialog = viewDialog;
        service = new FacultyServiceImpl(addPanel, editPanel, faPanel, viewDialog);
        faPanel.buttonListener(new ButtonEvent(), new PopupEvent(), new PopupEvent());
        addPanel.buttonListener(new ButtonEvent());
        editPanel.buttonListener(new ButtonEvent());

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
        this.editPanel.nmbr.addKeyListener(new KeyAdapter(){
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
            if (e.getSource() == faPanel.dd) {
                service.addButton();
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
            } else if (e.getSource() == faPanel.deleteFacultyMI) {
                service.delete();
            } else if (e.getSource() == faPanel.viewFacultyMI) {
                service.viewStudent();
            } else if (e.getSource() == viewDialog.dt) {
                service.editView();
            }
        }
    }

    class PopupEvent extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {
            service.facultyMouseEvent(e);
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            service.facultyPopupMenu();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }

    }
}
