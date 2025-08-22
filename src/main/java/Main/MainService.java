package Main;

import Student.StudentModel;
import java.util.Map;
import javax.swing.JTable;

public interface MainService {

    void loadSchedulesForToday();

    void loginButton();

    void checkAndLoadStudents();

    Map<String, StudentModel> preloadClassStudents(String scheduleCode);

    void checkAndVerifyStudents();
}
