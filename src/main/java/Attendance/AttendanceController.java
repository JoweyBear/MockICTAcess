package Attendance;

import Attendance.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        this.att.buttonListener(new ButtonEvent());
    }

    class ButtonEvent implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == att.ddClss) {
                service.addClassSched();
            }
            else if(e.getSource() == att.ddRm){
                service.addRoom();
            }
        }

    }
}
