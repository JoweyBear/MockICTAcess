package Analytics;

import javax.swing.table.DefaultTableModel;

public class AnalyticsServiceImpl implements AnalyticsService{

    AnalyticsPanel aPanel;
    AnalyticsDAO dao = new AnalyticsDAOImpl();
    
    public AnalyticsServiceImpl(AnalyticsPanel aPanel){
        this.aPanel = aPanel;
        
        setOverAllDataTable();
    }
    
    private void setOverAllDataTable(){
        DefaultTableModel model = dao.displayOverAllDificiencies();
        aPanel.jTable1.setModel(model);
        
    }
}
