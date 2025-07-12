package Faculty;

import javax.swing.table.DefaultTableModel;

public interface FacultyDAO {
    DefaultTableModel fetchAll();
    boolean save(FacultyModel faculty);
    boolean update(FacultyModel faculty);
    void delete(String stud_id);
    FacultyModel facultyView(String faculty_id);
}
