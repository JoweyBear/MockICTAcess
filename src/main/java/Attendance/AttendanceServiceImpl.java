package Attendance;

import Attendance.Views.*;
import java.awt.CardLayout;

public class AttendanceServiceImpl implements AttendanceService {

    AddRmPanel addroom;
    AddCSPanel addclass;
    EditRmPanel editroom;
    EditCSPanel editclass;
    AttendancePanel att;
    AttendanceDAO dao = new AttendanceDAOImpl();

    public AttendanceServiceImpl(AddRmPanel addroom, AddCSPanel addclass, EditRmPanel editroom, EditCSPanel editclass, AttendancePanel att) {
        this.addclass = addclass;
        this.addroom = addroom;
        this.editclass = editclass;
        this.editroom = editroom;
        this.att = att;

    }

    @Override
    public void addRoom() {
        CardLayout cl = (CardLayout) att.jPanel1.getLayout();
        att.jPanel1.add(addroom, "AddRoom");
        cl.show(att.jPanel1, "AddRoom");
    }

    @Override
    public void addClassSched() {
        CardLayout cl = (CardLayout) att.jPanel1.getLayout();
        att.jPanel1.add(addclass, "AddClass");
        cl.show(att.jPanel1, "AddClass");
    }

}
