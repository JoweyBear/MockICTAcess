package Attendance;


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
    void getSchedulesByDayAndTime();
    void getAttendaceBYScheduleId();
    void addClassSchedule();
    void updateClassSchedule();
    void deleteClassSchedule();
    void getStudentsByClassScheduleId();
    void addStudentToClassSchedule();
    void removeStudentFromClassSchedule();
}
