package Faculty;

import javax.swing.table.DefaultTableModel;

public interface FacultyDAO {
    DefaultTableModel fetchAll();
    void save(FacultyModel faculty);
    void update(FacultyModel faculty);
    void delete(String stud_id);
    FacultyModel FacultyView(String faculty_id);
}
