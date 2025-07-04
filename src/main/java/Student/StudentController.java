package Student;

import Student.Views.*;
import Student.Views.StudentPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class StudentController {

    AddStudPanel sAdd;
    EditStudPanel sEdit;
    StudentPanel sPanel;
    ViewStudentDialog viewDialog;
    StudentService sService;

    public StudentController(AddStudPanel sAdd, EditStudPanel sEdit, StudentPanel sPanel, ViewStudentDialog viewDialog) {
        this.sAdd = sAdd;
        this.sEdit = sEdit;
        this.sPanel = sPanel;
        this.viewDialog = viewDialog;
        sService = new StudentServiceImpl(sAdd, sEdit, sPanel, viewDialog);
        this.sAdd.buttonListener(new ButtonEvent());
        this.sEdit.buttonListener(new ButtonEvent());
        this.sPanel.buttonListener(new ButtonEvent(), new PopupEvent(), new PopupEvent());

        this.sAdd.nmbr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String number = sAdd.nmbr.getText();

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
        this.sEdit.nmbr.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String number = sEdit.nmbr.getText();

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
            if (e.getSource() == sPanel.dd) {
                sService.addButton();
//            } else if (e.getSource() == adminPanel.dt) {
//                service.editView();
//            } else if (e.getSource() == adminPanel.dlt) {
//                service.delete();
            } else if (e.getSource() == sAdd.scn) {
                sService.scanFingerAdd();
            } else if (e.getSource() == sAdd.sv) {
                sService.save();
            } else if (e.getSource() == sEdit.scn) {
                sService.scanFingerEdit();
            } else if (e.getSource() == sEdit.pdt) {
                sService.update();
            } else if (e.getSource() == sAdd.chssmg) {
                sService.selectImageForAdd();
            } else if (e.getSource() == sEdit.chssmg) {
                sService.selectImageForEdit();
            } else if (e.getSource() == sPanel.deleteStudentMI) {
                sService.delete();
            } else if (e.getSource() == sPanel.viewStudentMI) {
                sService.viewStudent();
            } else if (e.getSource() == viewDialog.dt) {
                sService.editView();
            }
        }

    }

    class PopupEvent extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {
            sService.studentMouseEvent(e);
        }

        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            sService.studentPopupMenu();
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }

    }
}
