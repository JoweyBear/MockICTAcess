package Main.Views;

import Utilities.RegisterFont;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class MainFrame extends javax.swing.JFrame {

    DisplayPanel panel;

    public MainFrame(DisplayPanel panel) {
        this.panel = panel;
        initComponents();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        jPanel7.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        jPanel2.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        jPanel6.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        jPanel5.putClientProperty(FlatClientProperties.STYLE, "arc: 20");
        jLabel1.setFont(RegisterFont.getFont("jost", 70));
        time.setFont(RegisterFont.getFont("jack", 50));
        jLabel5.setFont(RegisterFont.getFont("jost", 40));
        jLabel6.setFont(RegisterFont.getFont("jost", 40));
//        jLabel4.setFont(RegisterFont.getFont("jost", 40));

        CardLayout cl = (CardLayout) jPanel1.getLayout();
        jPanel1.add(panel, "displayPanel");
        cl.show(jPanel1, "displayPanel");
        subject.setText("");
        startTime.setText("");
        endTime.setText("");
        faculty.setText("");
        date.setText("");
        time.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLocaleChooser1 = new com.toedter.components.JLocaleChooser();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        scheduleTable = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        faculty = new javax.swing.JLabel();
        jPanel5 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        endTime = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        startTime = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        subject = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        time = new javax.swing.JLabel();
        date = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lgn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel3.setBackground(new java.awt.Color(65, 90, 119));

        scheduleTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        scheduleTable.setEnabled(false);
        jScrollPane1.setViewportView(scheduleTable);

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "ID", "First Name", "Last Name", "Time In", "Time Out"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        studentTable.setEnabled(false);
        jScrollPane2.setViewportView(studentTable);

        jPanel1.setLayout(new java.awt.CardLayout());

        jPanel2.setBackground(new java.awt.Color(71, 77, 93));

        jLabel9.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Faculty:");

        faculty.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        faculty.setForeground(new java.awt.Color(255, 255, 255));
        faculty.setText("faculty");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 57, Short.MAX_VALUE)
                .addComponent(faculty)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(faculty)
                    .addComponent(jLabel9))
                .addContainerGap())
        );

        jPanel5.setBackground(new java.awt.Color(71, 77, 93));

        jLabel8.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));
        jLabel8.setText("End Time:");

        endTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        endTime.setForeground(new java.awt.Color(255, 255, 255));
        endTime.setText("endTime");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8)
                .addGap(18, 18, Short.MAX_VALUE)
                .addComponent(endTime)
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(endTime)
                    .addComponent(jLabel8))
                .addContainerGap())
        );

        jPanel6.setBackground(new java.awt.Color(71, 77, 93));

        startTime.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        startTime.setForeground(new java.awt.Color(255, 255, 255));
        startTime.setText("startTime");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Start Time:");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(startTime)
                .addContainerGap())
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(startTime))
                .addContainerGap())
        );

        jPanel7.setBackground(new java.awt.Color(71, 77, 93));

        jLabel11.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("Course Number:");

        subject.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        subject.setForeground(new java.awt.Color(255, 255, 255));
        subject.setText("subject");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(subject)
                .addGap(10, 10, 10))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11)
                    .addComponent(subject))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addGap(14, 14, 14))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(10, 10, 10)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 443, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 452, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel4.setBackground(new java.awt.Color(13, 27, 42));
        jPanel4.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabel1.setFont(new java.awt.Font("Jost", 1, 60)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Welcome!");
        jPanel4.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 660, 470, 70));

        time.setFont(new java.awt.Font("JackInput", 0, 50)); // NOI18N
        time.setForeground(new java.awt.Color(255, 255, 255));
        time.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        time.setText("T I M E");
        jPanel4.add(time, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 470, 70));

        date.setFont(new java.awt.Font("Tahoma", 0, 35)); // NOI18N
        date.setForeground(new java.awt.Color(255, 255, 255));
        date.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        date.setText("D A T  E");
        jPanel4.add(date, new org.netbeans.lib.awtextra.AbsoluteConstraints(20, 100, 460, 70));

        jLabel5.setFont(new java.awt.Font("Jost", 1, 40)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Place your finger ");
        jPanel4.add(jLabel5, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 730, 470, 60));

        jLabel6.setFont(new java.awt.Font("Jost", 1, 40)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel6.setText("on the scanner.");
        jPanel4.add(jLabel6, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 790, 470, 60));

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/TrF1.png"))); // NOI18N
        jPanel4.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 390, 470, -1));

        lgn.setBackground(new java.awt.Color(13, 27, 42));
        lgn.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/usericon1.png"))); // NOI18N
        lgn.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jPanel4.add(lgn, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 10, -1, 50));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, 951, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    public void buttonListener(ActionListener a) {
        lgn.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JLabel date;
    public javax.swing.JLabel endTime;
    public javax.swing.JLabel faculty;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private com.toedter.components.JLocaleChooser jLocaleChooser1;
    public javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    public javax.swing.JPanel jPanel3;
    public javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    public javax.swing.JButton lgn;
    public javax.swing.JTable scheduleTable;
    public javax.swing.JLabel startTime;
    public javax.swing.JTable studentTable;
    public javax.swing.JLabel subject;
    public javax.swing.JLabel time;
    // End of variables declaration//GEN-END:variables
}
