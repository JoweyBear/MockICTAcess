package Utilities;

import Admin.AdminController;
import Admin.Views.*;
import AdminDashboard.Dashboard;
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
                .setDescription("ISUFST Fingerprint Smart Access System for Students");
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"Dashboard"},
            {"Manage", "Admin", "Student", "Faculty"},
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
                            LoginFrame frame = new LoginFrame();
                            Dashboard dashboard = new Dashboard();
                            dashboard.setVisible(true);
                            frame.setVisible(false);
                        } else if (index == 1) {
                            if (subIndex == 1) {
                                DisplayAdPanel aDisplay = new DisplayAdPanel();
                                AdminPanel adminPanel = new AdminPanel(aDisplay);
                                AddAdPanel aAddPanel = new AddAdPanel();
                                EditAdPanel sEditPanel = new EditAdPanel();
                                new AdminController(adminPanel, aAddPanel, sEditPanel);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(adminPanel, "Admin Panel");
                                cl.show(dashboard.jPanel2, "Admin Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("AdminPanel Clicked");
                            }
                            if (subIndex == 2) {
                                DisplayStudPanel sdisplay = new DisplayStudPanel();
                                StudentPanel studentPanel = new StudentPanel(sdisplay);
                                AddStudPanel addPanel = new AddStudPanel();
                                EditStudPanel editPanel = new EditStudPanel();
                                new StudentController(addPanel, editPanel, studentPanel);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(studentPanel, "Appointment Panel");
                                cl.show(dashboard.jPanel2, "Appointment Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("AdminPanel Clicked");
                            }
                            if (subIndex == 3) {
                                DisplayFaPanel fdisplay = new DisplayFaPanel();
                                FacultyPanel facultyPanel = new FacultyPanel(fdisplay);
                                AddFaPanel addPanel = new AddFaPanel();
                                EditFaPanel editPanel = new EditFaPanel();
                                new FacultyController(addPanel, editPanel, facultyPanel);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(facultyPanel, "Appointment Panel");
                                cl.show(dashboard.jPanel2, "Appointment Panel");
//                                adminPanel.setVisible(true);
                                System.out.println("AdminPanel Clicked");
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
//        return new SimpleFooterData()
//                .setTitle(GlobalVar.loggedInAdmin.getStFname()
//                        + " " + GlobalVar.loggedInAdmin.getStLname())
//                .setDescription(GlobalVar.loggedInAdmin.getPosition());

    }

    @Override
    public int getDrawerWidth() {
        return 500;
    }

}
