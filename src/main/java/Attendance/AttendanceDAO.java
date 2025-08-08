package Attendance;

import java.time.LocalTime;
import javax.swing.table.DefaultTableModel;

public interface AttendanceDAO {

    boolean saveRoom(AttModel att);

    boolean saveClassSched(AttModel att);

    boolean updateRoom(AttModel att);

    boolean updateClassSched(AttModel att);

    void deleteRoom(int room_id);

    void deleteClassSched(String cs_id);

    DefaultTableModel getByCSId(String csId);

    DefaultTableModel getByRoomId(int roomId);

    DefaultTableModel getAll();

    DefaultTableModel getAllRoom();

    DefaultTableModel getAllCS();

    DefaultTableModel getByDayAndTime(String day, LocalTime time);

    DefaultTableModel getByFacultyId(int facultyUserId);

    boolean addStudentToClassSchedule(String csId, String studentId);

    void removeStudentFromClassSchedule(String csId, String studentId);

}
