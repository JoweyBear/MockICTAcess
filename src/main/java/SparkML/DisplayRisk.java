package SparkML;

import Analytics.AnalyticsPanel;
import Student.StudentModel;
import Utilities.ChartDrawingSupplier;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Vector;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DisplayRisk {

    public static AnalyticsPanel panel = new AnalyticsPanel();

    public static void tableRender(Dataset<Row> highRiskStudents) {
        DefaultTableModel tableModel = (DefaultTableModel) panel.jTable1.getModel();
        String[] columns = {
            "Student ID", "Student Name", "Year", "Section",
            "Track", "Dropout Possibility"
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
            rowData.add(student_id);
            rowData.add(fullname);
            rowData.add(student.getYear());
            rowData.add(student.getSection());
            rowData.add(student.getTrack());

            Double rawProb = row.getAs("dropoutProbability");
            String formatted = String.format("%.2f%%", rawProb * 100);
            rowData.add(formatted);

            String father = row.getAs("father_employed");
            boolean isFatherEmployed = father.equalsIgnoreCase("yes");
            String mother = row.getAs("mother_employed");
            boolean isMottherEmployed = mother.equalsIgnoreCase("yes");

            // Optional: generate recommendation
//            String recommendation = RecommendationEngine.generate(row);
//            rowData.add(recommendation);
            tableModel.addRow(rowData);
        }

    }

    public static void panelRender(Dataset<Row> highRiskStudents) {
        panel.jPanel1.removeAll();
        panel.jPanel2.removeAll();
        panel.jPanel1.setLayout(new GridLayout(1, 1));
        panel.jPanel2.setLayout(new GridLayout(1, 1));

//        piechart
        Dataset<Row> genderCounts = highRiskStudents.groupBy("gender").count();
        List<Row> genderRows = genderCounts.collectAsList();
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        for (Row row : genderRows) {
            String gender = row.getString(0);
            long count = row.getLong(1);
            pieDataset.setValue(gender, count);
        }
        JFreeChart pieChart = ChartFactory.createPieChart("Gender Distribution of High-Risk Students",
                pieDataset,
                true, true, false
        );
        Plot piePlot = pieChart.getPlot();
        piePlot.setDrawingSupplier(new ChartDrawingSupplier());
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        panel.jPanel2.add(pieChartPanel);

//        barchart
        Dataset<Row> yearCounts = highRiskStudents.groupBy("year").count();
        List<Row> yearRows = yearCounts.collectAsList();
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (Row row : yearRows) {
            String year = row.getString(0);
            long count = row.getLong(1);
            barDataset.addValue(count, "Dropout Risk", year);
        }
        JFreeChart barChart = ChartFactory.createBarChart(
                "Dropout Risk by Year Level",
                "Year Level",
                "Number of Students",
                barDataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        CategoryPlot barPlot = barChart.getCategoryPlot();
        BarRenderer render = (BarRenderer) barPlot.getRenderer();
        render.setSeriesPaint(0, new Color(225, 219, 203));
        render.setSeriesPaint(1, new Color(95, 114, 148));
        render.setSeriesPaint(2, new Color(71, 77, 93));
        render.setSeriesPaint(3, new Color(146, 186, 230));
        render.setSeriesPaint(4, new Color(0, 51, 102));
        ChartPanel barChartPanel = new ChartPanel(barChart);
        panel.jPanel1.add(barChartPanel);

        panel.jPanel1.revalidate();
        panel.jPanel1.repaint();
        panel.jPanel2.revalidate();
        panel.jPanel2.repaint();
        panel.revalidate();
        panel.repaint();

    }
}
