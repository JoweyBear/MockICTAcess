package Student;

import Student.Views.AddPanel;
import Student.Views.EditPanel;
import Student.Views.StudentPanel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StudentController {

    AddPanel sAdd;
    EditPanel sEdit;
    StudentPanel sPanel;
    StudentService sService;

    public StudentController(AddPanel sAdd, EditPanel sEdit, StudentPanel sPanel) {
        this.sAdd = sAdd;
        this.sEdit = sEdit;
        this.sPanel = sPanel;
        sService = new StudentServiceImpl(sAdd, sEdit, sPanel);
        this.sAdd.buttonListener(new ButtonEvent());
        this.sEdit.buttonListener(new ButtonEvent());
        this.sPanel.buttonListener(new ButtonEvent());
    }
    class ButtonEvent implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        
    }
}
