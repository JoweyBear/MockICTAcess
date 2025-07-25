package Utilities;

import Admin.AdminController;
import Admin.Views.*;
import AdminDashboard.Dashboard;
import Attendance.AttendanceController;
import Attendance.Views.*;
import Faculty.FacultyController;
import Faculty.Views.*;
import Login.LoginFrame;
import Student.StudentController;
import Student.Views.*;
import Student.Views.StudentPanel;
import java.awt.CardLayout;
import raven.drawer.component.SimpleDrawerBuilder;
import raven.drawer.component.footer.SimpleFooterData;
import raven.drawer.component.header.SimpleHeaderData;
import raven.drawer.component.menu.MenuAction;
import raven.drawer.component.menu.MenuEvent;
import raven.drawer.component.menu.MenuValidation;
import raven.drawer.component.menu.SimpleMenuOption;
import raven.swing.AvatarIcon;

public class Drawerbuilder extends SimpleDrawerBuilder {

    private final Dashboard dashboard;

    public Drawerbuilder(Dashboard dashboard) {
        this.dashboard = dashboard;
    }

    @Override
    public SimpleHeaderData getSimpleHeaderData() {
        return new SimpleHeaderData()
                .setIcon(new AvatarIcon(getClass().getResource("/Images/BlckFingerprint1.png"), 100, 100, 999))
                .setTitle("ASmart ISUFST TouchPass")
                .setDescription("<html>ISUFST Fingerprint Smart<br>Access System for Students</html>");
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"Dashboard"},
            {"Manage", "Admin", "Student", "Faculty", "Attendance"},
            {"Reports", "Analytics"},
            {"Logout"}
        };

        return new SimpleMenuOption()
                .setMenus(menus)
                //                .setIcons(icons)
                .setBaseIconPath("raven/drawer/icon")
                .setIconScale(0.45f)
                .addMenuEvent(new MenuEvent() {
                    @Override
                    public void selected(MenuAction action, int index, int subIndex) {
                        if (index == 0) {
//                            Dashboard dashboard = new Dashboard();
//                            dashboard.setVisible(true);
                        } else if (index == 1) {
                            if (subIndex == 1) {
                                DisplayAdPanel aDisplay = new DisplayAdPanel();
                                AdminPanel adminPanel = new AdminPanel(aDisplay);
                                AddAdPanel aAddPanel = new AddAdPanel();
                                EditAdPanel aEditPanel = new EditAdPanel();
                                ViewAdminDialog aDialog = new ViewAdminDialog();
                                new AdminController(adminPanel, aAddPanel, aEditPanel, aDialog);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(adminPanel, "Admin Panel");
                                cl.show(dashboard.jPanel2, "Admin Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("AdminPanel Clicked");
                            }
                            if (subIndex == 2) {
                                DisplayStudPanel sdisplay = new DisplayStudPanel();
                                StudentPanel studentPanel = new StudentPanel(sdisplay);
                                AddStudPanel sAddPanel = new AddStudPanel();
                                EditStudPanel sEditPanel = new EditStudPanel();
                                ViewStudentDialog sDialog = new ViewStudentDialog();
                                new StudentController(sAddPanel, sEditPanel, studentPanel, sDialog);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(studentPanel, "Student Panel");
                                cl.show(dashboard.jPanel2, "Student Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("StudentPanel Clicked");
                            }
                            if (subIndex == 3) {
                                DisplayFaPanel fdisplay = new DisplayFaPanel();
                                FacultyPanel facultyPanel = new FacultyPanel(fdisplay);
                                AddFaPanel fAddPanel = new AddFaPanel();
                                EditFaPanel fEditPanel = new EditFaPanel();
                                ViewFacultyDialog fDialog = new ViewFacultyDialog();
                                new FacultyController(fAddPanel, fEditPanel, facultyPanel, fDialog);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(facultyPanel, "Faculty Panel");
                                cl.show(dashboard.jPanel2, "Faculty Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("Faculty Clicked");
                            }
                            if (subIndex == 4) {
                                DisplayAttPanel atdisplay = new DisplayAttPanel();
                                AttendancePanel attPanel = new AttendancePanel(atdisplay);
                                AddRmPanel rmaddPanel = new AddRmPanel();
                                AddCSPanel csaddPanel = new AddCSPanel();
                                EditRmPanel rmeditPanel = new EditRmPanel();
                                EditCSPanel cseditPanel = new EditCSPanel();
                                new AttendanceController(rmaddPanel, csaddPanel, rmeditPanel, cseditPanel, attPanel);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(attPanel, "Attendance Panel");
                                cl.show(dashboard.jPanel2, "Attendance Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("Attendance Clicked");
                            }
//                            Main.main.login();
                        }
                        System.out.println("Menu selected " + index + " " + subIndex);
                    }
                })
                .setMenuValidation(new MenuValidation() {
                    @Override
                    public boolean menuValidation(int index, int subIndex) {
//                        if(index==0){
//                            return false;
//                        }else if(index==3){
//                            return false;
//                        }
                        return true;
                    }

                });
    }

    @Override
    public SimpleFooterData getSimpleFooterData() {
        return new SimpleFooterData()
                .setTitle(GlobalVar.loggedInAdmin.getStFname()
                        + " " + GlobalVar.loggedInAdmin.getStLname())
                .setDescription(GlobalVar.loggedInAdmin.getCollge());

    }

//    @Override
//    public int getDrawerWidth() {
//        return 350;
//    }

}
