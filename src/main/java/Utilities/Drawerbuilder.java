package Utilities;

import Admin.AdminController;
import Admin.Views.*;
import AdminDashboard.Dashboard;
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
                .setDescription("ISUFST Fingerprint Smart Access System for Students")
                ;
    }

    @Override
    public SimpleMenuOption getSimpleMenuOption() {
        String menus[][] = {
            {"Dashboard"},
            {"Manage", "Admin", "Student"},
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
//                            WindowsTabbed.getInstance().addTab("Test Form", new TestForm());
                        } else if (index == 1) {
                            if (subIndex == 1) {
//                                AppointmentPanel aPanel = new AppointmentPanel();
//                                AddAppointment aAdd = new AddAppointment();
//                                UpdateAppointment aUpdate = new UpdateAppointment();
//                                new AppointmentController(aPanel, aAdd, aUpdate);
//                                CardLayout cl = (CardLayout) (jPanel1.getLayout());
//                                jPanel1.add(aPanel, "Appointment Panel");
//                                cl.show(jPanel1, "Appointment Panel");
                                DisplayPanel display = new DisplayPanel();
                                AdminPanel adminPanel = new AdminPanel(display);
                                AddPanel addPanel = new AddPanel();
                                EditPanel editPanel = new EditPanel();
                                new AdminController(adminPanel, addPanel, editPanel);
                                CardLayout cl = (CardLayout) (dashboard.jPanel2.getLayout());
                                dashboard.jPanel2.add(adminPanel, "Appointment Panel");
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
