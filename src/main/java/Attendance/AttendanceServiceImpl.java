package Attendance;

import Attendance.Views.*;
import Utilities.GlobalVar;
import Utilities.SearchDefaultModel;
import Utilities.StudentCBHandler;
import Utilities.TableDateFilter;
//import Utilities.*;
import java.awt.CardLayout;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class AttendanceServiceImpl implements AttendanceService {

    AddRmPanel addroom;
    AddCSPanel addclass;
    EditRmPanel editroom;
    EditCSPanel editclass;
    AttendancePanel att;
    AttendanceDAO dao = new AttendanceDAOImpl();
    private String college = GlobalVar.loggedInAdmin.getCollge();
    TableDateFilter dateFilter;

    public AttendanceServiceImpl(AddRmPanel addroom, AddCSPanel addclass, EditRmPanel editroom, EditCSPanel editclass, AttendancePanel att) {
        this.addclass = addclass;
        this.addroom = addroom;
        this.editclass = editclass;
        this.editroom = editroom;
        this.att = att;

        this.dateFilter = new TableDateFilter(att.jTable2, 5);
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
        new SearchDefaultModel(att, att.jTable1, att.srchTF, model);
    }

    @Override
    public void getRoomById() {
    }

    @Override
    public void saveRoom() {
        System.out.println("saveRoom clicked");
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

            boolean saveRM = dao.saveRoom(att);
            if (saveRM) {
                JOptionPane.showMessageDialog(null, "Room added successfully.");
                getAllRooms();
                clearAddRoom();
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Room can't be added.");
            }

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

            boolean updateRm = dao.updateRoom(att);
            if (updateRm) {
                getAllRooms();
                clearEditRoom();
                JOptionPane.showMessageDialog(null, "Room information updated succesfully");
            } else {
                JOptionPane.showMessageDialog(null, "An error occured. Rooom information can't be update.");
            }
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
        new SearchDefaultModel(att, att.jTable1, att.srchTF, model);

    }

    @Override
    public void getSchedulesByRoomId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int roomID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByRoomId(roomID);
            att.jTable2.setModel(model);
            new SearchDefaultModel(att, att.jTable2, att.srchTF, model);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a room to view.");
        }
    }

    @Override
    public void getSchedulesByFacultyId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void getScheduleAttDate() {
        dateFilter.applyFilter(att.jDateChooser1, att.jDateChooser2);
    }

    @Override
    public void getAttendaceBYScheduleId() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            int csID = (int) att.jTable1.getValueAt(dataRow, 0);
            DefaultTableModel model = dao.getByCSId(csID);
            att.jTable2.setModel(model);
            new SearchDefaultModel(att, att.jTable2, att.srchTF, model);

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

//            uncheck autoincrement in db
//            String subjectCode = addclass.crsID.getText().toUpperCase();
//            String section = addclass.sctn.getSelectedItem().toString().toUpperCase();
//            String yrLvl =  addclass.yr.getSelectedItem().toString().toUpperCase();
//            String track = addclass.trck;
//            ask for tracks, further clarification
            AttModel att = new AttModel();
            att.setClass_type(addclass.clssTyp.getText().trim());
            att.setDay(addclass.day.getSelectedItem().toString());
            att.setTime_strt(addclass.time1.getTime());
            att.setTime_end(addclass.time2.getTime());
            att.setSubject(addclass.crsID.getText().trim());
            att.setYear(addclass.yr.getSelectedItem().toString());
            att.setSection(addclass.sctn.getSelectedItem().toString());
            att.setCollege(college);

            String facultyid = addclass.cmbFclty.getSelectedItem().toString();
            String faculty_id = facultyid.split(" - ")[0].replace("Faculty ID: ", "").trim();
            att.setFaculty_id(faculty_id);
            System.out.println(faculty_id);

            String roomid = addclass.rmID.getSelectedItem().toString();
            String room_id = roomid.split(" - ")[0].replace("Room ID: ", "").trim();
            att.setRm_id(Integer.parseInt(room_id));
            System.out.println(room_id);

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
            new SearchDefaultModel(att, att.jTable2, att.srchTF, model);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a room to view.");
        }
    }

    @Override
    public void addStudentToClassSchedule() {
        JComboBox<String> studentCombo = new JComboBox<>();
        studentCombo.setEditable(true);

        try {
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
        } catch (IOException ex) {
            Logger.getLogger(AttendanceServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
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
                    + "remove student: " + student_id + " froom class schecule: " + cs_id + "?", "Warning", dialogButton);
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
        addclass.time1.setTime(null);
        addclass.time2.setTime(null);
        addclass.day.setSelectedIndex(0);
        addclass.cmbFclty.setSelectedIndex(0);
        addclass.clssTyp.setText("");
        addclass.crsID.setText("");
        addclass.yr.setSelectedIndex(0);
        addclass.sctn.setSelectedIndex(0);
        addclass.cmbFclty.setSelectedItem("");
        addclass.rmID.setSelectedItem("");
    }

    private void clearEditCS() {
        editclass.time1.setTime(null);
        editclass.time2.setTime(null);
        editclass.day.setSelectedIndex(0);
        editclass.cmbFclty.setSelectedIndex(0);
        editclass.crsID.setText("");
        editclass.clssTyp.setText("");
        editclass.sctn.setSelectedIndex(0);
        editclass.yr.setSelectedIndex(0);
        editclass.cmbFclty.setSelectedItem("");
        editclass.rmID.setSelectedItem("");
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

    private String getCellValue(int row, int col) {
        Object val = att.jTable1.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }

    @Override
    public void editRoom() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String room_id = getCellValue(dataRow, 0);
            editroom.jLabel4.setText(room_id);
            editroom.cllg.setSelectedItem(college);
            editroom.rmNm.setText(getCellValue(dataRow, 1));
            editroom.bldng.setText(getCellValue(dataRow, 2));
            editroom.flrLvl.setSelectedItem(getCellValue(dataRow, 3));
            editroom.typ.setSelectedItem(getCellValue(dataRow, 4));

            CardLayout cl = (CardLayout) att.jPanel1.getLayout();
            att.jPanel1.add(editroom, "EditRoom");
            cl.show(att.jPanel1, "EditRoom");

        } else {
            JOptionPane.showMessageDialog(null, "Please select Room to update.");
        }
    }

    @Override
    public void editCS() {
        int dataRow = att.jTable1.getSelectedRow();
        if (dataRow >= 0) {
            String cs_id = getCellValue(dataRow, 0);
            editclass.jLabel4.setText(cs_id);
            editclass.clssTyp.setText(getCellValue(dataRow, 1));
            editclass.crsID.setText(getCellValue(dataRow, 5));
            editclass.sctn.setSelectedItem(getCellValue(dataRow, 6));
            editclass.yr.setSelectedItem(getCellValue(dataRow, 7));
            editclass.day.setSelectedItem(getCellValue(dataRow, 2));
            String time1 = getCellValue(dataRow, 3);
            String time2 = getCellValue(dataRow, 4);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            LocalTime timeStart = LocalTime.parse(time1, formatter);
            LocalTime timeEnd = LocalTime.parse(time2, formatter);
            editclass.time1.setTime(timeStart);
            editclass.time2.setTime(timeEnd);
            editclass.cmbFclty.setSelectedItem(getCellValue(dataRow, 8));
            editclass.rmID.setSelectedItem(getCellValue(dataRow, 9));

            CardLayout cl = (CardLayout) att.jPanel1.getLayout();
            att.jPanel1.add(editclass, "EditClass");
            cl.show(att.jPanel1, "EditClass");

        } else {
            JOptionPane.showMessageDialog(null, "Please select Class Schedule to update.");
        }
    }

    @Override
    public void popupJTable1(MouseEvent e) {
        int r = att.jTable1.rowAtPoint(e.getPoint());
        if (r >= 0 && r < att.jTable1.getRowCount()) {
            att.jTable1.setRowSelectionInterval(r, r);
        } else {
            att.jTable1.clearSelection();
            return;
        }

        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            int column = att.jTable1.columnAtPoint(e.getPoint());
            String columnName = att.jTable1.getColumnName(column);

            if (columnName.equalsIgnoreCase("Room ID")) {
                att.jTable1RoomPopup.show(att.jTable1, e.getX(), e.getY());
            } else if (columnName.equalsIgnoreCase("Class Schedule ID")) {
                att.jTable1CSMenuPopup.show(att.jTable1, e.getX(), e.getY());
            }
        }
    }

    @Override
    public void popupJTable2(MouseEvent e) {
        int r = att.jTable2.rowAtPoint(e.getPoint());
        if (r >= 0 && r < att.jTable2.getRowCount()) {
            att.jTable2.setRowSelectionInterval(r, r);
        } else {
            att.jTable2.clearSelection();
            return;
        }

        int rowindex = att.jTable2.getSelectedRow();
        if (rowindex < 0) {
            return;
        }

        if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
            int column = att.jTable2.columnAtPoint(e.getPoint());
            String columnName = att.jTable2.getColumnName(column);
            if (columnName.equalsIgnoreCase("Student ID")) {
                att.jTable2CSPopup.show(att.jTable2, e.getX(), e.getY());
            } else if (columnName.equalsIgnoreCase("Subject")) {
                att.jTable2RmPopup.show(att.jTable2, e.getX(), e.getY());
            }
        }
    }

    public String generateScheduleId(String subjectCode, String section, String yearLevel, String track) {
        return subjectCode.toUpperCase() + section.toUpperCase() + yearLevel + track.toUpperCase();
    }
}
