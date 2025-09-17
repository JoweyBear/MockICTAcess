package SparkML;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class DataLoader {
    
    JTable table;
    
    public List<AttRecordsModel> extractFromJTable(JTable table) {
        List<AttRecordsModel> records = new ArrayList();
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        
        for (int row = 0; row < model.getRowCount(); row++) {
            AttRecordsModel record = new AttRecordsModel();
            record.setStudent_id(getCellValue(row, 0));
            record.setLate_count(Integer.parseInt(getCellValue(row, 5)));
            record.setAbsent_count(Integer.parseInt(getCellValue(row, 6)));
            
            records.add(record);
            
        }
        
        return records;
    }
    
    private String getCellValue(int row, int col) {
        Object val = table.getValueAt(row, col);
        return val != null ? val.toString() : "";
    }
}
