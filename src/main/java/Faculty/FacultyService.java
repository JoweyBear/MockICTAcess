package Faculty;

import java.awt.event.MouseEvent;

public interface FacultyService {
    void setTableData();

    void save();

    void editView();

    void viewStudent();

    void clearAdd();

    void clearEdit();

    void update();

    void delete();

    void addButton();

    void selectImageForAdd();

    void selectImageForEdit();

    void scanFingerAdd();

    void scanFingerEdit();

    void facultyPopupMenu();

    void facultyMouseEvent(MouseEvent e);
}
