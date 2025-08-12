package Main;

import javax.swing.JTable;

public interface MainService {

    void loginButton();

    void checkAndLoadStudents(JTable tableA, JTable tableB, int startCol, int endCol, int scheduleIdCol);
    
    void checkAndVerifyStudents();
}
