package Attendance;

import java.time.LocalTime;
import javax.swing.table.DefaultTableModel;

public interface AttendanceDAO {

    void saveRoom(AttModel att);
    void saveClassSched(AttModel att);
    void updateRoom(AttModel att);
    void updateClassSched(AttModel att);
    void deleteRoom(int room_id);
    void deleteClassSched(int cs_id);

//    AttModel getById(int csId);
//    DefaultTableModel getAll();
//    DefaultTableModel getByRoomId(int roomId);
//    DefaultTableModel getByFacultyId(int facultyUserId);
//    DefaultTableModel getByDayAndTime(String day, LocalTime time);
//    DefaultTableModel searchByName(String name);

}
