package Student;

import Student.Views.*;

public class StudentServiceImpl implements StudentService {

    AddPanel sAdd;
    EditPanel sEdit;
    StudentPanel sPanel;

    public StudentServiceImpl(AddPanel sAdd, EditPanel sEdit, StudentPanel sPanel) {
        this.sAdd = sAdd;
        this.sEdit = sEdit;
        this.sPanel = sPanel;
    }

}
