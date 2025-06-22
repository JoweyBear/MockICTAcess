package Admin;

import java.awt.event.MouseEvent;

public interface AdminService {

    void setTableData();

    void save();

    void editView();

    void viewAdmin();

    void clearAdd();

    void clearEdit();

    void update();

    void delete();

    void addButton();

    void selectImageForAdd();

    void selectImageForEdit();

    void scanFingerAdd();

    void scanFingerEdit();

    void adminPopupMenu();

    void adminMouseEvent(MouseEvent e);
//    void editButton();
}
