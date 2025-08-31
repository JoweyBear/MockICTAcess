package AdminDashboard;

import AdminDashboard.Views.*;
import Attendance.AttModel;
import Utilities.AttendanceFilterType;
import Utilities.GlobalVar;
import Utilities.SearchDefaultModel;
import java.awt.GridLayout;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
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
        JFreeChart chart = ChartFactory.createPieChart(title, dataset, true, true, Locale.getDefault());
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
        return new ChartPanel(chart);
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

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();

//        piechart
        Map<String, Integer> counts = dao.getAttendanceStatusCounts();
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));

//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceStatusByGender();
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();
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
        Map<String, Integer> counts = dao.getAttedanceCounts(AttendanceFilterType.BY_CLASS_SCHEDULE, cs_id);
        dp.jPanel6.add(buildPieChartPanel("Attendance Breakdown", counts));
//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceByGender(AttendanceFilterType.BY_CLASS_SCHEDULE, college, cs_id);
        dp.jPanel7.add(buildBarChartPanel("Gender Metrics", genderMap));

        repaintAndRevalidate();

    }

    @Override
    public void displayAttendanceBetweenAndCS() {
        String csid = dp.cs_id.getSelectedItem().toString();
        String cs_id = csid.split(" - ")[0].replace("Class Sched. ID: ", "").trim();
//        att.setRm_id(Integer.parseInt(cs_id));
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
    }
}
