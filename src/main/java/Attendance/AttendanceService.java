package Attendance;

import java.awt.event.MouseEvent;

public interface AttendanceService {

    void addRoom();

    void addClassSched();
//    void save();

    void getAllRooms();

    void getRoomById();

    void saveRoom();

    void updateRoom();

    void deleteRoom();

    void getAllClassSchedules();

    void getSchedulesByRoomId();

    void getSchedulesByFacultyId();

    void getScheduleAttDate();

    void getAttendaceBYScheduleId();

    void addClassSchedule();

    void updateClassSchedule();

    void deleteClassSchedule();

    void getStudentsByClassScheduleId();

    void addStudentToClassSchedule();

    void removeStudentFromClassSchedule();

    void jcomboSelection();

    void editRoom();

    void editCS();

    void popupJTable1(MouseEvent e);

    void popupJTable2(MouseEvent e);

}
