package AdminDashboard;

import AdminDashboard.Views.*;
import Attendance.AttModel;
import Utilities.AttendanceFilterType;
import Utilities.ChartDrawingSupplier;
import Utilities.GlobalVar;
import Utilities.SearchDefaultModel;
import com.mysql.cj.xdevapi.Row;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class DashboardServiceImpl implements DashboardService {

    Dashboard db;
    DashboardPanel dp;
    DashboardDAO dao = new DashboardDAOImpl();
    private String college = GlobalVar.loggedInAdmin.getCollge();

    public DashboardServiceImpl(Dashboard db, DashboardPanel dp) {
        this.db = db;
        this.dp = dp;

        this.dp.jPanel6.setLayout(new GridLayout(1, 1));
        this.dp.jPanel7.setLayout(new GridLayout(1, 1));

        setDashboardData();

    }

    private ChartPanel buildPieChartPanel(String title, Map<String, Integer> data) {
        DefaultPieDataset dataset = new DefaultPieDataset();
        data.forEach(dataset::setValue);
        JFreeChart chart = ChartFactory.createPieChart3D(title, dataset, true, true, Locale.getDefault());
        Plot plot = chart.getPlot();
        plot.setDrawingSupplier(new ChartDrawingSupplier());
        return new ChartPanel(chart);
    }

    private ChartPanel buildBarChartPanel(String title, Map<String, Map<String, Integer>> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.forEach((gender, statusMap) -> {
            statusMap.forEach((status, count) -> {
                dataset.addValue(count, gender, status);
            });
        });
        JFreeChart chart = ChartFactory.createBarChart(title,
                "Status",
                "Attendance Count",
                dataset,
                PlotOrientation.VERTICAL, true, true, true);
        CategoryPlot plot = chart.getCategoryPlot();
        BarRenderer render = (BarRenderer) plot.getRenderer();
        render.setSeriesPaint(0, new Color(225, 219, 203));
        render.setSeriesPaint(1, new Color(95, 114, 148));
        render.setSeriesPaint(2, new Color(71, 77, 93));
        render.setSeriesPaint(3, new Color(146, 186, 230));
        render.setSeriesPaint(4, new Color(0, 51, 102));
        return new ChartPanel(chart);
    }
    
//    add attendance in the morning and afternoon all attendance Records just like the irregCounts
    
    
    void clearComponents(){
        dp.jDateChooser1.setDate(null);
        dp.jDateChooser2.setDate(null);
        dp.jDateChooser3.setDate(null);
        dp.cmbFaculty.setSelectedIndex(-1);
        dp.cs_id.setSelectedIndex(-1);
        dp.cs_id1.setSelectedIndex(-1);
        
    }

    private void repaintAndRevalidate() {
        dp.jPanel6.revalidate();
        dp.jPanel7.revalidate();
        dp.jPanel6.repaint();
        dp.jPanel7.repaint();
        dp.repaint();
        dp.revalidate();
    }

    private void setDashboardData() {
        DefaultTableModel model = dao.getAllAttendanceRecords();
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        AttModel att = dao.getAttendanceStatus();

        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.incomplete.setText(String.valueOf(att.getIncompleteCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));
        dp.leftEarly.setText(String.valueOf(att.getLeftEarly()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> counts = dao.getAttendanceStatusCounts();
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));

//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceStatusByGender();
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();
        System.out.println("SetDashboardData");
    }

    @Override
    public void displayIrregularAttendance() {
        DefaultTableModel model = dao.getAllIrregularAttendance();
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();
        dp.jPanel6.setLayout(new GridLayout(1, 1));
        dp.jPanel7.setLayout(new GridLayout(1, 1));

//        barchart1
        Map<String, Map<String, Integer>> irregAttCounts = dao.getAllIrregularAttendanceByGender();
        dp.jPanel6.add(buildBarChartPanel("GenderMetrics", irregAttCounts));

//        barchart2
        Map<String, Map<String, Integer>> irregCounts = dao.getAllIrregularAttendancePerSubject();
        dp.jPanel7.add(buildBarChartPanel("Subject Metrics", irregCounts));

        repaintAndRevalidate();
    }

    @Override
    public void displayAttendanceBetween() {
        Date utilDate1 = dp.jDateChooser1.getDate();
        Date utilDate2 = dp.jDateChooser2.getDate();

        java.sql.Date date1 = new java.sql.Date(utilDate1.getTime());
        java.sql.Date date2 = new java.sql.Date(utilDate2.getTime());

        if (date1 == null && date2 == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No date is selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        String date1 = sdf.format(selectedDate1);
//        String date2 = sdf.format(selectedDate2);

        DefaultTableModel model = dao.getAttendanceRecords(AttendanceFilterType.BY_DATE_RANGE, college, date1, date2);
//        DefaultTableModel model = dao.getAllAttendaceRecordsBetween(date1, date2);
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        AttModel att = dao.getAttStatusCounts(AttendanceFilterType.BY_DATE_RANGE, college, date1, date2);
//        AttModel att = dao.getAttendanceStatusBetween(date1, date2);
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));
        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.leftEarly.setText(String.valueOf(att.getLeftEarly()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> counts = dao.getAttedanceCounts(AttendanceFilterType.BY_DATE_RANGE, college, date1, date2);
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));
//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceByGender(AttendanceFilterType.BY_DATE_RANGE, college, date1, date2);
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();
        clearComponents();
    }

    @Override
    public void displayAttendanceCS() {
        String csid = dp.cs_id.getSelectedItem().toString();
        String cs_id = csid.split(" - ")[0].replace("Class Sched. ID: ", "").trim();
//        att.setRm_id(Integer.parseInt(cs_id));
        System.out.println(cs_id);

        DefaultTableModel model = dao.getAttendanceRecords(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id);
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        AttModel att = dao.getAttStatusCounts(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id);
//        AttModel att = dao.getAttendanceStatusBetween(date1, date2);
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));
        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.leftEarly.setText(String.valueOf(att.getLeftEarly()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> counts = dao.getAttedanceCounts(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id);
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));
//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceByGender(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id);
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();
        clearComponents();
    }

    @Override
    public void displayAttendanceBetweenAndCS() {
        String csid = dp.cs_id.getSelectedItem().toString();
        String cs_id = csid.split(" - ")[0].replace("Class Sched. ID: ", "").trim();
        if (cs_id == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No Class Schedule Selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println(cs_id);

        Date utilDate1 = dp.jDateChooser1.getDate();
        Date utilDate2 = dp.jDateChooser2.getDate();

        java.sql.Date date1 = new java.sql.Date(utilDate1.getTime());
        java.sql.Date date2 = new java.sql.Date(utilDate2.getTime());

        if (date1 == null && date2 == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No date is selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = dao.getAttendanceRecords(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id, date1, date2);
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        AttModel att = dao.getAttStatusCounts(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id, date1, date2);
//        AttModel att = dao.getAttendanceStatusBetween(date1, date2);
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));
        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.leftEarly.setText(String.valueOf(att.getLeftEarly()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> counts = dao.getAttedanceCounts(AttendanceFilterType.BY_CLASS_SCHEDULE, cs_id, date1, date2);
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));
//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceByGender(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id, date1, date2);
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();
        clearComponents();
    }

    @Override
    public void dsiplayAttendanceByFaculty() {
        String facultyId = dp.cmbFaculty.getSelectedItem().toString();
        String faculty_id = facultyId.split(" - ")[0].replace("Faculty ID: ", "").trim();
        if (faculty_id == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No Faculty is Selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        System.out.println(faculty_id);
        DefaultTableModel model = dao.getAttendanceRecords(AttendanceFilterType.BY_FACULTY, college, faculty_id);
        dp.jTable1.setModel(model);
        new SearchDefaultModel(dp, dp.jTable1, dp.search, model);

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> statusCounts = dao.getAttedanceCounts(AttendanceFilterType.BY_FACULTY, college, faculty_id);
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", statusCounts));

//        barchart2
        Map<String, Map<String, Integer>> irregCounts = dao.getAllAttendancePerSubjectByFaculty(college, faculty_id);
        dp.jPanel7.add(buildBarChartPanel("Subject Metrics", irregCounts));

        repaintAndRevalidate();
        clearComponents();
    }

    @Override
    public void saveAttendance() {
        String csid = dp.cs_id1.getSelectedItem().toString();
        String cs_id = csid.split(" - ")[0].replace("Class Sched. ID: ", "").trim();
        if (cs_id == null) {
            
            JOptionPane.showMessageDialog(null, "Attendance", "No Class Schedule Selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Date date = dp.jDateChooser3.getDate();

        if (date == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No date is selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DefaultTableModel model = dao.saveAttendance(college, cs_id, date);
        if (model == null || model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "No attendance records to export.");
            return;
        }

        // Format date for filename
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(date);
        String defaultFileName = "Attendance_" + cs_id + "_" + formattedDate + ".csv";

        // Confirm before saving
        String message = "Are you sure you want to save this attendance?\n\n"
                + "Date: " + formattedDate + "\n"
                + "Class Schedule ID: " + cs_id;
        int confirm = JOptionPane.showConfirmDialog(null, message, "Confirm Export", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION) {
            System.out.println("Export cancelled.");
            return;
        }

        // File chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Attendance CSV");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(null);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = fileChooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
        }

        // Write CSV
        try (PrintWriter pw = new PrintWriter(fileToSave)) {
            // Header
            for (int col = 0; col < model.getColumnCount(); col++) {
                pw.print(model.getColumnName(col));
                if (col < model.getColumnCount() - 1) {
                    pw.print(",");
                }
            }
            pw.println();

            // Rows
            for (int row = 0; row < model.getRowCount(); row++) {
                for (int col = 0; col < model.getColumnCount(); col++) {
                    Object value = model.getValueAt(row, col);
                    pw.print(value != null ? value.toString().replace(",", " ") : "");
                    if (col < model.getColumnCount() - 1) {
                        pw.print(",");
                    }
                }
                pw.println();
            }

            JOptionPane.showMessageDialog(null, " Exported successfully to:\n" + fileToSave.getAbsolutePath());
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, " Export failed:\n" + e.getMessage());
            e.printStackTrace();
        }

    }
}
