package AdminDashboard;

import AdminDashboard.Views.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import jpos.util.PopupListener;

public class DashboardController {
    
    Dashboard dashboard;
    DashboardPanel panel;
    DashboardService service;
    
    public DashboardController(Dashboard dashboard, DashboardPanel panel){
        this.dashboard = dashboard;
        this.panel = panel;
        
        service = new DashboardServiceImpl(dashboard, panel);
        
        this.panel.buttonListener(new ButtonEvent(), new PopupEvent(), new PopupEvent());
    }
    class ButtonEvent implements ActionListener{

        @Override
        public void actionPerformed(ActionEvent e) {
            if(e.getSource() == panel.disInAb){
                service.displayIrregularAttendance();
            }else if(e.getSource() == panel.srchCs){
                service.displayAttendanceCS();
            }else if(e.getSource() == panel.saveAtt){
                service.saveAttendance();
            }else if(e.getSource() == panel.srchCsDate){
                service.displayAttendanceBetweenAndCS();
            }else if(e.getSource() == panel.srchDate){
                service.displayAttendanceBetween();
            }else if(e.getSource() == panel.srchFaculty){
                service.dsiplayAttendanceByFaculty();
            }
        }
        
    }
    
    class PopupEvent extends MouseAdapter implements PopupMenuListener{

        @Override
        public void mouseReleased(MouseEvent e){
            
        }
        @Override
        public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        }

        @Override
        public void popupMenuCanceled(PopupMenuEvent e) {
        }
        
    }

}
