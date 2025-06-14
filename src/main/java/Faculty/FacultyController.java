package Faculty;

import Faculty.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FacultyController {

    AddFaPanel addPanel;
    EditFaPanel editPanel;
    FacultyPanel faPanel;
    FacultyService service;

    public FacultyController(AddFaPanel addPanel, EditFaPanel editPanel, FacultyPanel faPanel) {
        this.addPanel = addPanel;
        this.editPanel = editPanel;
        this.faPanel = faPanel;
        service = new FacultyServiceImpl(addPanel, editPanel, faPanel);
        addPanel.buttonListener(new ButtonEvent());
        editPanel.buttonListener(new ButtonEvent());
    }
    class ButtonEvent implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
        }
        
    }
}
