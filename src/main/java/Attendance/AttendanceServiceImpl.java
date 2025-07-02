package Attendance;

import Attendance.Views.*;
import Utilities.*;
import java.awt.CardLayout;
import java.time.LocalTime;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class AttendanceServiceImpl implements AttendanceService {

    AddRmPanel addroom;
    AddCSPanel addclass;
    EditRmPanel editroom;
    EditCSPanel editclass;
    AttendancePanel att;
    AttendanceDAO dao = new AttendanceDAOImpl();
    private String college = GlobalVar.loggedInAdmin.getCollge();

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
        new QuickSearchList(att, att.jTable1, att.srchTF, (List<List<String>>) model);
    }

    @Override
    public void getRoomById() {
    }

    @Override
    public void saveRoom() {
        if (addroom.bldng.getText().trim().equals("")
                || addroom.cllg.getSelectedItem().equals("College")
                || addroom.dscp.getText().trim().equals("")
                || addroom.flrLvl.getSelectedItem().equals("Floor Level")
                || addroom.rmNm.getText().trim().equals("")
                || addroom.typ.getSelectedItem().equals("Type of Room")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AttModel att = new AttModel();
            att.setName(addroom.rmNm.getText().trim());
            att.setCollege(addroom.cllg.getSelectedItem().toString());
            att.setBuilding(addroom.bldng.getText().trim());
            att.setFlr_lvl(addroom.flrLvl.getSelectedItem().toString());
            att.setType(addroom.typ.getSelectedItem().toString());
            att.setDesc(addroom.dscp.getText().trim());
            att.setCollege(college);
            dao.saveRoom(att);
            getAllRooms();
            clearAddRoom();
        }
    }

    @Override
    public void updateRoom() {
        if (editroom.bldng.getText().trim().equals("")
                || editroom.cllg.getSelectedItem().equals("College")
                || editroom.dscp.getText().trim().equals("")
                || editroom.flrLvl.getSelectedItem().equals("Floor Level")
                || editroom.rmNm.getText().trim().equals("")
                || editroom.typ.getSelectedItem().equals("Type of Room")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AttModel att = new AttModel();
            att.setName(editroom.rmNm.getText().trim());
            att.setCollege(editroom.cllg.getSelectedItem().toString());
            att.setBuilding(editroom.bldng.getText().trim());
            att.setFlr_lvl(editroom.flrLvl.getSelectedItem().toString());
            att.setType(editroom.typ.getSelectedItem().toString());
            att.setDesc(editroom.dscp.getText().trim());
            dao.updateRoom(att);
            getAllRooms();
            clearEditRoom();
        }
    }

    @Override
    public void deleteRoom() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int room_id = (int) att.jTable1.getValueAt(dataRow, 0);
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "Delete Room: " + room_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.deleteRoom(room_id);
                getAllRooms();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select room to delete.");
        }
    }

    @Override
    public void getAllClassSchedules() {
        DefaultTableModel model = dao.getAllCS();
        att.jTable1.setModel(model);
        new QuickSearchList(att, att.jTable1, att.srchTF, (List<List<String>>) model);

    }

    @Override
    public void getSchedulesByRoomId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int roomID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByRoomId(roomID);
            att.jTable2.setModel(model);
            new QuickSearchList(att, att.jTable2, att.srchTF, (List<List<String>>) model);
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
            new QuickSearchList(att, att.jTable2, att.srchTF, (List<List<String>>) model);

        } else {
            JOptionPane.showMessageDialog(null, "Please select a class chedule to view.");
        }
    }

    @Override
    public void addClassSchedule() {
        if (addclass.day.getSelectedItem().equals("Day")
                || addclass.cmbFclty.getSelectedItem().equals("")
                || addclass.clssTyp.getText().trim().equals("")
                || addclass.yr.getSelectedItem().equals("Year")
                || addclass.sctn.getSelectedItem().equals("Section")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AttModel att = new AttModel();
            att.setClass_type(addclass.clssTyp.getText().trim());
            att.setDay(addclass.day.getSelectedItem().toString());
            att.setTime_strt(addclass.time1.getTime());
            att.setTime_end(addclass.time2.getTime());
            att.setSubject(addclass.crsID.getText().trim());
            att.setYear(addclass.yr.getSelectedItem().toString());
            att.setSection(addclass.sctn.getSelectedItem().toString());

            String facultyid = addclass.cmbFclty.getSelectedItem().toString();
            String faculty_id = facultyid.split(" - ")[0].replace("ID: ", "").trim();
            att.setFaculty_id(faculty_id);

            String roomid = addclass.rmID.getSelectedItem().toString();
            String room_id = roomid.split(" - ")[0].replace("ID: ", "").trim();
            att.setRm_id(Integer.parseInt(room_id));

            dao.saveClassSched(att);
            getAllClassSchedules();
            clearAddCS();
        }
    }

    @Override
    public void updateClassSchedule() {
        if (editclass.day.getSelectedItem().equals("Day")
                || editclass.cmbFclty.getSelectedItem().equals("")
                || editclass.clssTyp.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(null, "Fields cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            AttModel att = new AttModel();
            att.setClass_type(editclass.clssTyp.getText().trim());
            att.setDay(editclass.day.getSelectedItem().toString());
            att.setTime_strt(editclass.time1.getTime());
            att.setTime_end(editclass.time2.getTime());
            att.setSubject(editclass.crsID.getText().trim());
            att.setYear(addclass.yr.getSelectedItem().toString());
            att.setSection(addclass.sctn.getSelectedItem().toString());

            String facultyid = editclass.cmbFclty.getSelectedItem().toString();
            String faculty_id = facultyid.split(" - ")[0].replace("ID: ", "").trim();
            att.setFaculty_id(faculty_id);

            String roomid = editclass.rmID.getSelectedItem().toString();
            String room_id = roomid.split(" - ")[0].replace("ID: ", "").trim();
            att.setRm_id(Integer.parseInt(room_id));

            dao.updateClassSched(att);
            getAllClassSchedules();
            clearEditCS();
        }
    }

    @Override
    public void deleteClassSchedule() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int cs_id = (int) att.jTable1.getValueAt(dataRow, 0);
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "Delete ClassSchedule: " + cs_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.deleteClassSched(cs_id);
                getAllClassSchedules();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select class schedule to delete.");
        }
    }

    @Override
    public void getStudentsByClassScheduleId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int csID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByCSId(csID);
            att.jTable2.setModel(model);
            new QuickSearchList(att, att.jTable2, att.srchTF, (List<List<String>>) model);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a room to view.");
        }
    }

    @Override
    public void addStudentToClassSchedule() {
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setEditable(true);

        studentCombo.getEditor().getEditorComponent().addKeyListener(new StudentCBHandler(studentCombo));
        int selectedRow = att.jTable2.getSelectedRow();
        int classScheduleId = (int) att.jTable2.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(
                null,
                studentCombo,
                "Select Student to Add",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) studentCombo.getSelectedItem();
            if (selected != null) {
                int idStart = selected.indexOf("ID:") + 3;
                int idEnd = selected.indexOf(" -");
                String studentId = selected.substring(idStart, idEnd).trim();

                boolean added = dao.addStudentToClassSchedule(classScheduleId, studentId);
                if (added) {
                    JOptionPane.showMessageDialog(null, "Student added successfully.");
                } else {
                    JOptionPane.showMessageDialog(null, "Student is already in this class.");
                }
            }
        }

    }

    @Override
    public void removeStudentFromClassSchedule() {
        int dataRow = att.jTable2.getSelectedRow();
        if (dataRow >= 0) {
            int cs_id = (int) att.jTable2.getValueAt(dataRow, 0);
            String student_id = att.jTable2.getValueAt(dataRow, 1).toString();
            int dialogButton = JOptionPane.YES_NO_OPTION;
            int dialogResult = JOptionPane.showConfirmDialog(null, "Would You Like to "
                    + "remove student: " + cs_id + " froom class schecule: " + student_id + "?", "Warning", dialogButton);
            if (dialogResult == JOptionPane.YES_OPTION) {
                dao.removeStudentFromClassSchedule(cs_id, student_id);
                getStudentsByClassScheduleId();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select class schedule to delete.");
        }
    }

    private void clearAddRoom() {
        addroom.bldng.setText("");
        addroom.cllg.setSelectedIndex(0);
        addroom.dscp.setText("");
        addroom.flrLvl.setSelectedIndex(0);
        addroom.rmNm.setText("");
        addroom.typ.setSelectedIndex(0);
    }

    private void clearEditRoom() {
        editroom.bldng.setText("");
        editroom.cllg.setSelectedIndex(0);
        editroom.dscp.setText("");
        editroom.flrLvl.setSelectedIndex(0);
        editroom.rmNm.setText("");
        editroom.typ.setSelectedIndex(0);
    }

    private void clearAddCS() {
        addclass.time1.setTimeToNow();
        addclass.time2.setTimeToNow();
        addclass.day.setSelectedIndex(0);
        addclass.cmbFclty.setSelectedIndex(0);
        addclass.clssTyp.setText("");
        addclass.crsID.setText("");
        addclass.yr.setSelectedIndex(0);
        addclass.sctn.setSelectedIndex(0);
    }

    private void clearEditCS() {
        editclass.time1.setTimeToNow();
        editclass.time2.setTimeToNow();
        editclass.day.setSelectedIndex(0);
        editclass.cmbFclty.setSelectedIndex(0);
        editclass.crsID.setText("");
        editclass.clssTyp.setText("");
        editclass.sctn.setSelectedIndex(0);
        editclass.yr.setSelectedIndex(0);
    }

    @Override
    public void jcomboSelection() {
        String selected = att.ar.getSelectedItem().toString();
        if ("Room".equals(selected)) {
            getAllRooms();
        } else if ("Class Schedule".equals(selected)) {
            getAllClassSchedules();
        }
    }

    @Override
    public void editRoom() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String room_id = att.jTable1.getValueAt(dataRow, 0).toString();
            String college = GlobalVar.loggedInAdmin.getCollge();
            editroom.jLabel4.setText(room_id);
            editroom.cllg.setSelectedItem(college);
            editroom.rmNm.setText(att.jTable1.getValueAt(dataRow, 1).toString());
            editroom.bldng.setText(att.jTable1.getValueAt(dataRow, 2).toString());
            editroom.flrLvl.setSelectedItem(att.jTable1.getValueAt(dataRow, 3).toString());
            editroom.typ.setSelectedItem(att.jTable1.getValueAt(dataRow, 4).toString());

        } else {
            JOptionPane.showMessageDialog(null, "Please select admin to update.");
        }
    }

    @Override
    public void editCS() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String cs_id = att.jTable1.getValueAt(dataRow, 0).toString();
            editclass.jLabel4.setText(cs_id);
            editclass.clssTyp.setText(att.jTable1.getValueAt(dataRow, 1).toString());
            editclass.crsID.setText(att.jTable1.getValueAt(dataRow, 2).toString());
            editclass.sctn.setSelectedItem(att.jTable1.getValueAt(dataRow, 3).toString());
            editclass.yr.setSelectedItem(att.jTable1.getValueAt(dataRow, 4).toString());
            editclass.day.setSelectedItem(att.jTable1.getValueAt(dataRow, 5).toString());
            editclass.time1.setTime((LocalTime) att.jTable1.getValueAt(dataRow, 6));
            editclass.time2.setTime((LocalTime) att.jTable1.getValueAt(dataRow, 7));
            editclass.cmbFclty.setSelectedItem(att.jTable1.getValueAt(dataRow, 8).toString());
            editclass.rmID.setSelectedItem(att.jTable1.getValueAt(dataRow, 9).toString());

        }
    }
}
