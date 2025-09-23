package SparkML;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;

public class RiskTableRenderer {

    public static void render(Dataset<Row> highRiskStudents) {
        // Convert Spark Dataset<Row> to TableModel
        DefaultTableModel tableModel = new DefaultTableModel();
        String[] columns = {
            "Student ID", "Student Name", "Year", "Section",
            "Track", "Dropout Possibility", "Possible Reason"
        };
        for (String col : columns) {
            tableModel.addColumn(col);
        }

//        List<Rows> row = highRiskStudents.collectAsList();
        // Collect rows from Spark and populate the table
        for (Row row : highRiskStudents.collectAsList()) {
            Vector<Object> rowData = new Vector<>();
            rowData.add(row.getAs("studentId"));
            rowData.add(row.getAs("gpa"));
            rowData.add(row.getAs("lateCounts"));
            rowData.add(row.getAs("absentCounts"));
            rowData.add(row.getAs("failedSubjects"));
            rowData.add(row.getAs("academicStatus"));
            rowData.add(row.getAs("degreeProgram"));
            rowData.add(row.getAs("prediction"));

            // Optional: generate recommendation
//            String recommendation = RecommendationEngine.generate(row);
//            rowData.add(recommendation);

            tableModel.addRow(rowData);
        }

        // Create JTable and display in a scrollable panel
        JTable table = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(table);

        JFrame frame = new JFrame("High-Risk Students");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(1000, 400);
        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.setVisible(true);
    }
}
