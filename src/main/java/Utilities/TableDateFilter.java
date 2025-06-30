package Utilities;

import com.toedter.calendar.JDateChooser;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import javax.swing.RowFilter;
import java.util.Date;

public class TableDateFilter {

    private final JTable table;
    private final int dateColumnIndex;

    public TableDateFilter(JTable table, int dateColumnIndex) {
        this.table = table;
        this.dateColumnIndex = dateColumnIndex;
    }

    public void applyFilter(JDateChooser fromDateChooser, JDateChooser toDateChooser) {
        Date fromDate = fromDateChooser.getDate();
        Date toDate = toDateChooser.getDate();

        if (fromDate == null || toDate == null) {
            JOptionPane.showMessageDialog(null, "Please select both From and To dates.");
            return;
        }

        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>((DefaultTableModel) table.getModel());
        table.setRowSorter(sorter);

        sorter.setRowFilter(new RowFilter<DefaultTableModel, Integer>() {
            @Override
            public boolean include(Entry<? extends DefaultTableModel, ? extends Integer> entry) {
                Object value = entry.getValue(dateColumnIndex);
                try {
                    if (value instanceof Date) {
                        Date rowDate = (Date) value;
                        return !rowDate.before(fromDate) && !rowDate.after(toDate);
                    } else {
                        throw new IllegalArgumentException("Date column contains invalid data.");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(null,
                            "Invalid date format in the date column.\n"
                            + "Filtering not applicable.",
                            "Date Format Error",
                            JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        });
    }

    public void clearFilter() {
        TableRowSorter<?> sorter = (TableRowSorter<?>) table.getRowSorter();
        if (sorter != null) {
            sorter.setRowFilter(null);
        }
    }
}
