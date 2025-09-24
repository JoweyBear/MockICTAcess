package SparkML;

import Student.StudentModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import java.util.List;

public class RiskTableRenderer {

    public static void render(Dataset<Row> highRiskStudents) {
        // Convert Spark Dataset<Row> to TableModel
        DefaultTableModel tableModel = new DefaultTableModel();
        String[] columns = {
            "Student ID", "Student Name", "Year", "Section",
            "Track", "Dropout Possibility", "Possible Reason", "Recommendation"
        };
        for (String col : columns) {
            tableModel.addColumn(col);
        }

        List<Row> rows = highRiskStudents.collectAsList();
        // Collect rows from Spark and populate the table
        for (Row row : rows) {
            Vector<Object> rowData = new Vector<>();
            String student_id = row.getAs("studentId");
            StudentModel student = ParamDataLoader.getStudentInfo(student_id);
            String fname = student.getFname();
            String mname = student.getMname();
            String lname = student.getLname();
            String fullname = fname + " " + mname + " " + lname;
            rowData.add(fullname);
            rowData.add(student.getYear());
            rowData.add(student.getSection());
            rowData.add(student.getTrack());

            Double rawProb = row.getAs("dropoutProbability");
            String formatted = String.format("%.2f%%", rawProb * 100);
            rowData.add(formatted);
            
//            rowData.add();


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
