package Attendance.Views;

import Utilities.RegisterFont;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import javax.swing.event.PopupMenuListener;

public class AttendancePanel1 extends javax.swing.JPanel {

    DisplayAttPanel display;

    public AttendancePanel1(DisplayAttPanel display) {
        this.display = display;
        initComponents();
        CardLayout cl = (CardLayout) jPanel1.getLayout();
        jPanel1.add(display, "DisplayPanel");
        cl.show(jPanel1, "DisplayPanel");
        srchTF.putClientProperty("JTextField.placeholderText", "Search here...");
        ddClss.setFont(RegisterFont.getFont("nstr", 14));
        ddRm.setFont(RegisterFont.getFont("nstr", 14));
        fltr.setFont(RegisterFont.getFont("nstr", 14));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTable1RoomPopup = new javax.swing.JPopupMenu();
        editRmMI = new javax.swing.JMenuItem();
        viewRMMI = new javax.swing.JMenuItem();
        jTable2CSPopup = new javax.swing.JPopupMenu();
        addStudentCSMI = new javax.swing.JMenuItem();
        removeStudentCSMI = new javax.swing.JMenuItem();
        jTable1CSMenuPopup = new javax.swing.JPopupMenu();
        editCSMI = new javax.swing.JMenuItem();
        viewCSMI = new javax.swing.JMenuItem();
        jTable2RmPopup = new javax.swing.JPopupMenu();
        addCStoRMMI = new javax.swing.JMenuItem();
        removeCStoRMMI = new javax.swing.JMenuItem();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        ddClss = new javax.swing.JButton();
        ddRm = new javax.swing.JButton();
        ar = new javax.swing.JComboBox<>();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        fltr = new javax.swing.JButton();
        srchTF = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();

        editRmMI.setText("Edit Room");
        jTable1RoomPopup.add(editRmMI);

        viewRMMI.setText("View");
        jTable1RoomPopup.add(viewRMMI);

        addStudentCSMI.setText("Add Student");
        jTable2CSPopup.add(addStudentCSMI);

        removeStudentCSMI.setText("Remove Student");
        jTable2CSPopup.add(removeStudentCSMI);

        editCSMI.setText("Edit Class Schedule");
        jTable1CSMenuPopup.add(editCSMI);

        viewCSMI.setText("View");
        jTable1CSMenuPopup.add(viewCSMI);

        addCStoRMMI.setText("Add Class Schedule");
        jTable2RmPopup.add(addCStoRMMI);

        removeCStoRMMI.setText("Remove Class Schedule");
        jTable2RmPopup.add(removeCStoRMMI);

        setBackground(new java.awt.Color(119, 141, 169));

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

        jDateChooser1.setDateFormatString("yyyy-MM-dd");
        jDateChooser1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jDateChooser2.setDateFormatString("yyyy-MM-dd");
        jDateChooser2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("From");

        jLabel2.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("to");

        fltr.setText("Filter");
        fltr.setToolTipText("");

        srchTF.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

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
        jTable1.setMinimumSize(new java.awt.Dimension(0, 0));
        jTable1.setPreferredSize(new java.awt.Dimension(240, 235));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(ar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                        .addComponent(ddClss)
                        .addGap(17, 17, 17)
                        .addComponent(ddRm))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(srchTF, javax.swing.GroupLayout.PREFERRED_SIZE, 1, Short.MAX_VALUE)
                        .addGap(189, 189, 189))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 473, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(fltr)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(srchTF))
                        .addGap(570, 570, 570))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2)
                            .addComponent(fltr, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(ddClss, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(ddRm)
                                    .addComponent(ar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(jScrollPane2))
                        .addGap(19, 19, 19))))
        );
    }// </editor-fold>//GEN-END:initComponents
    public void buttonListener(ActionListener a, PopupMenuListener b1, MouseListener c1, PopupMenuListener b2, MouseListener c2) {
        ddClss.addActionListener(a);
        ddRm.addActionListener(a);
        fltr.addActionListener(a);
        jTable1.addMouseListener(c1);
        jTable2.addMouseListener(c2);
        jTable1CSMenuPopup.addPopupMenuListener(b1);
        jTable1RoomPopup.addPopupMenuListener(b1);
        jTable2CSPopup.addPopupMenuListener(b2);
        jTable2RmPopup.addPopupMenuListener(b2);
        ar.addActionListener(a);
        addCStoRMMI.addActionListener(a);
        addStudentCSMI.addActionListener(a);
        editCSMI.addActionListener(a);
        editRmMI.addActionListener(a);
        removeCStoRMMI.addActionListener(a);
        removeStudentCSMI.addActionListener(a);
        viewRMMI.addActionListener(a);
        viewCSMI.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JMenuItem addCStoRMMI;
    public javax.swing.JMenuItem addStudentCSMI;
    public javax.swing.JComboBox<String> ar;
    public javax.swing.JButton ddClss;
    public javax.swing.JButton ddRm;
    public javax.swing.JMenuItem editCSMI;
    public javax.swing.JMenuItem editRmMI;
    public javax.swing.JButton fltr;
    public com.toedter.calendar.JDateChooser jDateChooser1;
    public com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JTable jTable1;
    public javax.swing.JPopupMenu jTable1CSMenuPopup;
    public javax.swing.JPopupMenu jTable1RoomPopup;
    public javax.swing.JTable jTable2;
    public javax.swing.JPopupMenu jTable2CSPopup;
    public javax.swing.JPopupMenu jTable2RmPopup;
    public javax.swing.JMenuItem removeCStoRMMI;
    public javax.swing.JMenuItem removeStudentCSMI;
    public javax.swing.JTextField srchTF;
    public javax.swing.JMenuItem viewCSMI;
    public javax.swing.JMenuItem viewRMMI;
    // End of variables declaration//GEN-END:variables
}
