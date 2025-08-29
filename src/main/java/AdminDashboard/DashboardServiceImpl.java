package AdminDashboard;

import AdminDashboard.Views.*;
import Attendance.AttModel;
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

    public DashboardServiceImpl(Dashboard db, DashboardPanel dp) {
        this.db = db;
        this.dp = dp;

        setDashboardData();

    }

    private void setDashboardData() {
        DefaultTableModel model = dao.getAllAttendanceRecords();
        dp.jTable1.setModel(model);

        AttModel att = dao.getAttendanceStatus();

        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.incomplete.setText(String.valueOf(att.getIncompleteCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();
        dp.jPanel6.setLayout(new GridLayout(1, 1));
        dp.jPanel7.setLayout(new GridLayout(1, 1));

//        piechart
        Map<String, Integer> counts = dao.getAttendanceStatusCounts();
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        counts.forEach(pieDataset::setValue);
        JFreeChart pieChart = ChartFactory.createPieChart("Attendance Breakdown", pieDataset, true, true, Locale.getDefault());
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        dp.jPanel6.add(pieChartPanel);

//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceStatusByGender();
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (String gender : genderMap.keySet()) {
            Map<String, Integer> statusMap = genderMap.get(gender);
            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
                barDataset.addValue(entry.getValue(), gender, entry.getKey());
            }
        }
        JFreeChart barChart = ChartFactory.createBarChart("Gender Metrics",
                "Status",
                "Attendance Count",
                barDataset,
                PlotOrientation.VERTICAL, true, true, true);
        ChartPanel barChartPanel = new ChartPanel(barChart);
        dp.jPanel7.add(barChartPanel);

        dp.jPanel6.revalidate();
        dp.jPanel7.revalidate();
        dp.jPanel6.repaint();
        dp.jPanel7.repaint();
        dp.repaint();
        dp.revalidate();

    }

    @Override
    public void displayIrregularAttendance() {
        DefaultTableModel model = dao.getAllIrregularAttendance();
        dp.jTable1.setModel(model);

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();
        dp.jPanel6.setLayout(new GridLayout(1, 1));
        dp.jPanel7.setLayout(new GridLayout(1, 1));

//        barchart1
        Map<String, Map<String, Integer>> irregAttCounts = dao.getAllIrregularAttendanceByGender();
        DefaultCategoryDataset genderBarDataset = new DefaultCategoryDataset();
        for (String gender : irregAttCounts.keySet()) {
            Map<String, Integer> irregGenderCounts = irregAttCounts.get(gender);
            for (Map.Entry<String, Integer> entry : irregGenderCounts.entrySet()) {
                genderBarDataset.addValue(entry.getValue(), gender, entry.getKey());
            }
        }
        JFreeChart genderBarChart = ChartFactory.createBarChart("Gender Metrics", "Status", "Attendance Count", genderBarDataset, PlotOrientation.VERTICAL, true, true, true);
        ChartPanel genderBarChartPanel = new ChartPanel(genderBarChart);
        dp.jPanel6.add(genderBarChartPanel);

//        barchart2
        Map<String, Map<String, Integer>> irregCounts = dao.getAllIrregularAttendancePerSubject();
        DefaultCategoryDataset subjectBarDataset = new DefaultCategoryDataset();
        for (String subject : irregCounts.keySet()) {
            Map<String, Integer> irregSubjectCounts = irregCounts.get(subject);
            for (Map.Entry<String, Integer> entry : irregSubjectCounts.entrySet()) {
                subjectBarDataset.addValue(entry.getValue(), subject, entry.getKey());
            }
        }
        JFreeChart subjectBarChart = ChartFactory.createBarChart("Subject Metrics", "Status", "Attendance Count", genderBarDataset, PlotOrientation.VERTICAL, true, true, true);
        ChartPanel subjectBarChartPanel = new ChartPanel(subjectBarChart);
        dp.jPanel7.add(subjectBarChartPanel);

        dp.jPanel6.revalidate();
        dp.jPanel7.revalidate();
        dp.jPanel6.repaint();
        dp.jPanel7.repaint();
        dp.revalidate();
        dp.repaint();

    }

    @Override
    public void displayAttendanceBetween() {
        Date selectedDate1 = dp.jDateChooser1.getDate();
        Date selectedDate2 = dp.jDateChooser2.getDate();
        if (selectedDate1 == null && selectedDate2 == null) {
            JOptionPane.showMessageDialog(null, "Attendance", "No date is selected.", JOptionPane.WARNING_MESSAGE);
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = sdf.format(selectedDate1);
        String date2 = sdf.format(selectedDate2);

        DefaultTableModel model = dao.getAllAttendaceRecordsBetween(date1, date2);
        dp.jTable1.setModel(model);

        AttModel att = dao.getAttendanceStatusBetween(date1, date2);
        dp.timeIn.setText(String.valueOf(att.getTimeInCount()));
        dp.timeOut.setText(String.valueOf(att.getTimeOutCount()));
        dp.late.setText(String.valueOf(att.getLateCount()));
        dp.absent.setText(String.valueOf(att.getAbsentCount()));
        dp.leftEarly.setText(String.valueOf(att.getLeftEarly()));

        dp.jPanel6.removeAll();
        dp.jPanel7.removeAll();
        dp.jPanel6.setLayout(new GridLayout(1, 1));
        dp.jPanel7.setLayout(new GridLayout(1, 1));
//        piechart
        Map<String, Integer> counts = dao.getAttendanceCountsBetween(date1, date2);
        DefaultPieDataset pieDataset = new DefaultPieDataset();
        counts.forEach(pieDataset::setValue);
        JFreeChart pieChart = ChartFactory.createPieChart("Attendance Breakdown", pieDataset, true, true, Locale.getDefault());
        ChartPanel pieChartPanel = new ChartPanel(pieChart);
        dp.jPanel6.add(pieChartPanel);
//        barchart
        Map<String, Map<String, Integer>> genderMap = dao.getAttendanceByGenderBetween(date1, date2);
        DefaultCategoryDataset barDataset = new DefaultCategoryDataset();
        for (String gender : genderMap.keySet()) {
            Map<String, Integer> statusMap = genderMap.get(gender);
            for (Map.Entry<String, Integer> entry : statusMap.entrySet()) {
                barDataset.addValue(entry.getValue(), gender, entry.getKey());
            }
        }
        JFreeChart barChart = ChartFactory.createBarChart("Gender Metrics",
                "Status",
                "Attendance Count",
                barDataset,
                PlotOrientation.VERTICAL, true, true, true);
        ChartPanel barChartPanel = new ChartPanel(barChart);
        dp.jPanel7.add(barChartPanel);

        dp.jPanel6.revalidate();
        dp.jPanel7.revalidate();
        dp.jPanel6.repaint();
        dp.jPanel7.repaint();
        dp.repaint();
        dp.revalidate();

    }
}
