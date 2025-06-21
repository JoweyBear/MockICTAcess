package Admin;

import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.event.PopupMenuListener;

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
