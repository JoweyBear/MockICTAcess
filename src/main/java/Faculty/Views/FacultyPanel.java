package Faculty.Views;

import Utilities.RegisterFont;
import com.formdev.flatlaf.FlatLaf;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.UIManager;
import javax.swing.event.PopupMenuListener;

public class FacultyPanel extends javax.swing.JPanel {

    DisplayFaPanel display;

    public FacultyPanel(DisplayFaPanel display) {
        this.display = display;
        UIManager.put("Button.foreground", Color.white);
        FlatLaf.updateUI();
        initComponents();
        CardLayout cl = (CardLayout) jPanel2.getLayout();
        jPanel2.add(display, "DisplayPanel");
        cl.show(jPanel2, "DisplayPanel");
        srchtxtfld.setBackground(new Color(255, 255, 255));
        srchtxtfld.putClientProperty("JTextField.placeholderText", "Search here...");
        dd.setFont(RegisterFont.getFont("nstr", 14));
// 

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        facultyPopup = new javax.swing.JPopupMenu();
        viewFacultyMI = new javax.swing.JMenuItem();
        deleteFacultyMI = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        dd = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        srchtxtfld = new javax.swing.JTextField();

        viewFacultyMI.setText("View Faculty");
        facultyPopup.add(viewFacultyMI);

        deleteFacultyMI.setText("Delete");
        facultyPopup.add(deleteFacultyMI);

        jPanel1.setBackground(new java.awt.Color(119, 141, 169));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);
        jTable1.getAccessibleContext().setAccessibleParent(this);

        dd.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        dd.setText("Add");

        jPanel2.setLayout(new java.awt.CardLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(srchtxtfld, javax.swing.GroupLayout.PREFERRED_SIZE, 470, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                        .addComponent(dd, javax.swing.GroupLayout.PREFERRED_SIZE, 170, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3))
                    .addComponent(jScrollPane1))
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(33, 33, 33)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(srchtxtfld, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dd, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 735, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(37, 37, 37))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    public void buttonListener(ActionListener a, PopupMenuListener b, MouseListener c) {
        dd.addActionListener(a);
        deleteFacultyMI.addActionListener(a);
        viewFacultyMI.addActionListener(a);
        jTable1.addMouseListener(c);
        facultyPopup.addPopupMenuListener(b);
//        dt.addActionListener(a);
//        dlt.addActionListener(a);
//        srch.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton dd;
    public javax.swing.JMenuItem deleteFacultyMI;
    public javax.swing.JPopupMenu facultyPopup;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JTable jTable1;
    public javax.swing.JTextField srchtxtfld;
    public javax.swing.JMenuItem viewFacultyMI;
    // End of variables declaration//GEN-END:variables
}
