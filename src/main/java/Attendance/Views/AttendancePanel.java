package Attendance.Views;

import Utilities.RegisterFont;
import java.awt.CardLayout;
import java.awt.event.ActionListener;

public class AttendancePanel extends javax.swing.JPanel {

    DisplayAttPanel display;

    public AttendancePanel(DisplayAttPanel display) {
        this.display = display;
        initComponents();
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        jPanel1.add(display, "DisplayPanel");
        cl.show(jPanel1, "DisplayPanel");
        ddClss.setFont(RegisterFont.getFont("nstr", 14));
        ddRm.setFont(RegisterFont.getFont("nstr", 14));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTable1Popup = new javax.swing.JPopupMenu();
        editCsMI = new javax.swing.JMenuItem();
        addStudentCSMI = new javax.swing.JMenuItem();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        ddClss = new javax.swing.JButton();
        ddRm = new javax.swing.JButton();
        ar = new javax.swing.JComboBox<>();

        editCsMI.setText("Edit");
        jTable1Popup.add(editCsMI);

        addStudentCSMI.setText("jMenuItem1");
        jTable1Popup.add(addStudentCSMI);

        setBackground(new java.awt.Color(119, 141, 169));

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

        jPanel1.setPreferredSize(new java.awt.Dimension(240, 235));
        jPanel1.setLayout(new java.awt.CardLayout());

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
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
        jScrollPane2.setViewportView(jTable2);

        ddClss.setText("Add Class");
        ddClss.setToolTipText("");

        ddRm.setText("Add Room");

        ar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        ar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Category", "Class Schedule", "Room" }));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(ddClss)
                        .addGap(17, 17, 17)
                        .addComponent(ddRm))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 375, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 596, Short.MAX_VALUE)
                        .addGap(6, 6, 6))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(ddClss)
                            .addComponent(ddRm)
                            .addComponent(ar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(13, 13, 13))
        );
    }// </editor-fold>//GEN-END:initComponents
    public void buttonListener(ActionListener a) {
        ddClss.addActionListener(a);
        ddRm.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem addStudentCSMI;
    public javax.swing.JComboBox<String> ar;
    public javax.swing.JButton ddClss;
    public javax.swing.JButton ddRm;
    private javax.swing.JMenuItem editCsMI;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTable jTable1;
    private javax.swing.JPopupMenu jTable1Popup;
    public javax.swing.JTable jTable2;
    // End of variables declaration//GEN-END:variables
}
