package Student;

import javax.swing.table.DefaultTableModel;

public interface StudentDAO {
    DefaultTableModel fetchAll(String collegeOfLoggedInAdmin);
    boolean save(StudentModel student);
    boolean update(StudentModel student);
    void delete(String stud_id);
    StudentModel studentView(String stud_id);
}
