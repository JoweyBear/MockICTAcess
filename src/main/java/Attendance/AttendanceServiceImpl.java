package Attendance;

import Attendance.Views.*;
import Utilities.QuickSearchList;
import java.awt.CardLayout;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

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

    @Override
    public void getAllRooms() {
        DefaultTableModel model = dao.getAllRoom();
        att.jTable1.setModel(model);
//        new QuickSearchList(att, att.jTable1, att.srchtxtfld, (List<List<String>>) model);
    }

    @Override
    public void getRoomById() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody

    }

    @Override
    public void saveRoom() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateRoom() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteRoom() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getAllClassSchedules() {
        DefaultTableModel model = dao.getAllCS();
        att.jTable1.setModel(model);
    }

    @Override
    public void getSchedulesByRoomId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int roomID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByRoomId(roomID);
            att.jTable2.setModel(model);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a room to view.");
        }
    }

    @Override
    public void getSchedulesByFacultyId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getSchedulesByDayAndTime() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getAttendaceBYScheduleId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int csID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByCSId(csID);
            att.jTable2.setModel(model);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a class chedule to view.");
        }
    }

    @Override
    public void addClassSchedule() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void updateClassSchedule() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void deleteClassSchedule() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getStudentsByClassScheduleId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void addStudentToClassSchedule() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void removeStudentFromClassSchedule() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
