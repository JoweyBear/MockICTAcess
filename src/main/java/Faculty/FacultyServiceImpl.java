package Faculty;

import Faculty.Views.*;

public class FacultyServiceImpl implements FacultyService {

    AddFaPanel addPanel;
    EditFaPanel editPanel;
    FacultyPanel faPanel;
    FacultyDAO dao = new FacultyDAOImpl();

    public FacultyServiceImpl(AddFaPanel addPanel, EditFaPanel editPanel, FacultyPanel faPanel) {
        this.addPanel =  addPanel;
        this.editPanel = editPanel;
        this.faPanel = faPanel;
    }
}
