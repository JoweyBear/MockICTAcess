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
            }else if(e.getSource() == att.fltr){
                service.getScheduleAttDate();
            }else if(e.getSource() == att.ar){
                service.jcomboSelection();
            }else if(e.getSource() == att.addStudentCSMI){
                service.addStudentToClassSchedule();
            }else if(e.getSource() == att.addCStoRMMI){
                service.addClassSchedule();
            }else if(e.getSource() == att.editCSMI){
                service.editCS();
            }else if(e.getSource() == att.editRmMI){
                service.editRoom();
            }else if(e.getSource() == att.removeCStoRMMI){
                service.deleteClassSchedule();
            }else if(e.getSource() == att.removeStudentCSMI){
                service.removeStudentFromClassSchedule();
            }else if(e.getSource() == att.viewCSMI){
                service.getSchedulesByRoomId();
            }else if(e.getSource() == att.viewRMMI){
                service.getRoomById();
            }else if(e.getSource() == addclass.dd){
               service.addClassSchedule();
            }else if(e.getSource() == editclass.pdt){
                service.updateClassSchedule();
            }else if(e.getSource() == addroom.dd){
                service.saveRoom();
            }else if(e.getSource() == editroom.pdt){
                service.updateRoom();
            }
        }
    }
    class JTable1Popup extends MouseAdapter implements PopupMenuListener{

        @Override
        public void mouseReleased(MouseEvent e){
            service.popupJTable1(e);
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
            service.popupJTable2(e);
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
