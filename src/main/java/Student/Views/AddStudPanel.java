package Student.Views;

import Utilities.RegisterFont;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.UIManager;

public class AddStudPanel extends javax.swing.JPanel {

    public AddStudPanel() {
        UIManager.put("TextField.background", Color.white);
        initComponents();

        student_id.putClientProperty("JTextField.placeholderText", "Student ID");
        adfname.putClientProperty("JTextField.placeholderText", "First Name");
        admname.putClientProperty("JTextField.placeholderText", "Middle Name");
        adlname.putClientProperty("JTextField.placeholderText", "Last Name");
        nmbr.putClientProperty("JTextField.placeholderText", "Contact Number");
        ml.putClientProperty("JTextField.placeholderText", "Email");
        brgy.putClientProperty("JTextField.placeholderText", "Barangay");
        municipal.putClientProperty("JTextField.placeholderText", "Municipality");
        trck.putClientProperty("JTextField.placeholderText", "Track(for courses with track only)");
//        bdy.putClientProperty("JDateChooser.placeholderText", "Birthday");


        jLabelimage.setText("");
        jLabelfinger.setText("");
        chssmg.setFont(RegisterFont.getFont("nstr", 14));
        scn.setFont(RegisterFont.getFont("nstr", 14));
        sv.setFont(RegisterFont.getFont("nstr", 14));
        clr.setFont(RegisterFont.getFont("nstr", 14));

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jPanel1 = new javax.swing.JPanel();
        adlname = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        adfname = new javax.swing.JTextField();
        admname = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        student_id = new javax.swing.JTextField();
        jLabelimage = new javax.swing.JLabel();
        sv = new javax.swing.JButton();
        scn = new javax.swing.JButton();
        clr = new javax.swing.JButton();
        nmbr = new javax.swing.JTextField();
        jLabelfinger = new javax.swing.JLabel();
        chssmg = new javax.swing.JButton();
        sx = new javax.swing.JComboBox<>();
        bdy = new com.toedter.calendar.JDateChooser();
        ml = new javax.swing.JTextField();
        cllg = new javax.swing.JComboBox<>();
        yr = new javax.swing.JComboBox<>();
        sctn = new javax.swing.JComboBox<>();
        brgy = new javax.swing.JTextField();
        municipal = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        trck = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(119, 141, 169));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(13, 27, 42), 2, true), "Add Student", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("SansSerif", 1, 36))); // NOI18N

        adlname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Personal Infromatiom:");

        adfname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        admname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("University Information");

        student_id.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabelimage.setText("jLabel3");

        sv.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        sv.setText("save");

        scn.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        scn.setText("Scan");

        clr.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        clr.setText("clear");
        clr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clrActionPerformed(evt);
            }
        });

        nmbr.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabelfinger.setText("jLabel3");

        chssmg.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        chssmg.setText("Choose Image");

        sx.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Sex", "Female", "Male" }));

        ml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cllg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "College", "CICT", "CIT", "CM", "CHM", "CTM", "COED" }));
        cllg.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cllgActionPerformed(evt);
            }
        });

        yr.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Year", "I", "II", "III", "IV" }));

        sctn.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Section", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L" }));

        brgy.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        municipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Birthday:");

        trck.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cllg, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(yr, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(sctn, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(admname)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(adfname)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(student_id)
                    .addComponent(sx, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(adlname)
                    .addComponent(nmbr)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(scn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabelfinger, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE))
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabelimage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                            .addComponent(chssmg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(ml)
                    .addComponent(brgy)
                    .addComponent(municipal)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(trck, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(36, 36, 36))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(105, 105, 105)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(clr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(110, 110, 110))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(student_id)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cllg)
                    .addComponent(yr)
                    .addComponent(sctn))
                .addGap(8, 8, 8)
                .addComponent(trck)
                .addGap(18, 18, 18)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(adfname)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(admname)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adlname)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nmbr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ml)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(brgy)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(municipal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sx)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bdy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelfinger, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(jLabelimage, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scn)
                    .addComponent(chssmg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(sv, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(4, 4, 4)
                .addComponent(clr, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clrActionPerformed
        student_id.setText("");
        adfname.setText("");
        admname.setText("");
        adlname.setText("");
        nmbr.setText("");
        ml.setText("");
        sx.setSelectedIndex(0);
        cllg.setSelectedIndex(0);
        yr.setSelectedIndex(0);
        sctn.setSelectedIndex(0);
        jLabelimage.setText("");
        jLabelfinger.setText("");
        brgy.setText("");
        municipal.setText("");
        trck.setText("");
    }//GEN-LAST:event_clrActionPerformed

    private void cllgActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cllgActionPerformed
      String selectedCollege = cllg.getSelectedItem().toString();
      
      if("CICT".equals(selectedCollege)){
          trck.setEnabled(true);
          trck.requestFocus();
      }else{
          trck.setEnabled(false);
          trck.setText("");
      }
    }//GEN-LAST:event_cllgActionPerformed
    public void buttonListener(ActionListener a) {
        sv.addActionListener(a);
        scn.addActionListener(a);
        chssmg.addActionListener(a);
    }
    
    public void keyListener(KeyListener evt){
        nmbr.addKeyListener(evt);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField adfname;
    public javax.swing.JTextField adlname;
    public javax.swing.JTextField admname;
    public com.toedter.calendar.JDateChooser bdy;
    public javax.swing.JTextField brgy;
    public javax.swing.JButton chssmg;
    public javax.swing.JComboBox<String> cllg;
    public javax.swing.JButton clr;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    public javax.swing.JLabel jLabelfinger;
    public javax.swing.JLabel jLabelimage;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JTextField ml;
    public javax.swing.JTextField municipal;
    public javax.swing.JTextField nmbr;
    public javax.swing.JButton scn;
    public javax.swing.JComboBox<String> sctn;
    public javax.swing.JTextField student_id;
    public javax.swing.JButton sv;
    public javax.swing.JComboBox<String> sx;
    public javax.swing.JTextField trck;
    public javax.swing.JComboBox<String> yr;
    // End of variables declaration//GEN-END:variables
}
