package AdminDashboard;

import AdminDashboard.Views.*;

public class DashboardController {
    
    Dashboard dashboard;
    DashboardPanel panel;
    DashboardService service;
    
    public DashboardController(Dashboard dashboard, DashboardPanel panel){
        this.dashboard = dashboard;
        this.panel = panel;
        
        service = new DashboardServiceImpl(dashboard, panel);
    }

}
