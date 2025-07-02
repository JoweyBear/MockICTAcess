package Attendance;

import Attendance.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class AttendanceController {

    AddRmPanel addroom;
    AddCSPanel addclass;
    EditRmPanel editroom;
    EditCSPanel editclass;
    AttendancePanel att;
    AttendanceService service;

    public AttendanceController(AddRmPanel addroom, AddCSPanel addclass, EditRmPanel editroom, EditCSPanel editclass, AttendancePanel att) {
        this.addclass = addclass;
        this.addroom = addroom;
        this.editclass = editclass;
        this.editroom = editroom;
        this.att = att;
        service = new AttendanceServiceImpl(addroom, addclass, editroom, editclass, att);
        this.att.buttonListener(new ButtonEvent(),
                new JTable1Popup(), new JTable1Popup(),
                new JTable2Popup(), new JTable2Popup());
    }

    class ButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == att.ddClss) {
                service.addClassSched();
            } else if (e.getSource() == att.ddRm) {
                service.addRoom();
            }
        }
    }
    class JTable1Popup extends MouseAdapter implements PopupMenuListener{

        @Override
        public void mouseReleased(MouseEvent e){
            
        }
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
    }
    class JTable2Popup extends MouseAdapter implements PopupMenuListener{

        @Override
        public void mouseReleased(MouseEvent e){
            
        }
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
    }
    }
