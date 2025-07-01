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
                new JTable1CSPopup(), new JTable1CSPopup(),
                new JTable1RoomPopup(), new JTable1RoomPopup(),
                new JTable2CSPopup(), new JTable2CSPopup(),
                new JTable2RoomPopup(), new JTable2RoomPopup());
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

    class JTable1CSPopup extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {

        }

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

    class JTable1RoomPopup extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {

        }

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

        class JTable2RoomPopup extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {

        }

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
    class JTable2CSPopup extends MouseAdapter implements PopupMenuListener {

        @Override
        public void mouseReleased(MouseEvent e) {

        }

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
