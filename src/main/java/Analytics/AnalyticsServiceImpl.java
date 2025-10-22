package Analytics;

import SparkML.Prediction;
import java.awt.BorderLayout;
import java.awt.Dialog;
import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class AnalyticsServiceImpl implements AnalyticsService {

    AnalyticsPanel aPanel;
    AnalyticsDAO dao = new AnalyticsDAOImpl();

    public AnalyticsServiceImpl(AnalyticsPanel aPanel) {
        this.aPanel = aPanel;

//        setOverAllDataTable();
    }

//    private void setOverAllDataTable(){
//        DefaultTableModel model = dao.displayOverAllDificiencies();
//        aPanel.jTable1.setModel(model);
//        
//    }
    @Override
    public void runAnalysis() {
        // Create modal dialog
        JDialog loadingDialog = new JDialog(SwingUtilities.getWindowAncestor(aPanel), "Analyzing...", Dialog.ModalityType.APPLICATION_MODAL);
        loadingDialog.setUndecorated(true); // Optional: remove window borders

        // Create progress bar
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setString("Running analysis...");
        progressBar.setStringPainted(true);

        // Layout
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        content.add(progressBar, BorderLayout.CENTER);
        loadingDialog.setContentPane(content);
        loadingDialog.pack();
        loadingDialog.setLocationRelativeTo(aPanel);

        // Run analysis in background
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                Prediction.runAnalysis();
//                runAnalysis(); // Your long-running method
                return null;
            }

            @Override
            protected void done() {
                loadingDialog.dispose(); // Close modal when done
            }
        };

        worker.execute();
        loadingDialog.setVisible(true); // Blocks until worker is done

    }
}