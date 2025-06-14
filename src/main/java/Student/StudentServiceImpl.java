package Student;

import Student.Views.*;

public class StudentServiceImpl implements StudentService {

    AddStudPanel sAdd;
    EditStudPanel sEdit;
    StudentPanel sPanel;

    public StudentServiceImpl(AddStudPanel sAdd, EditStudPanel sEdit, StudentPanel sPanel) {
        this.sAdd = sAdd;
        this.sEdit = sEdit;
        this.sPanel = sPanel;
    }

}
