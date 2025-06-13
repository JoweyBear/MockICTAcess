package Admin.Views;

import Utilities.RegisterFont;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.UIManager;

public class EditAdPanel extends javax.swing.JPanel {

    public EditAdPanel() {
        UIManager.put("TextField.background", Color.white);
        initComponents();

        admin_id.putClientProperty("JTextField.placeholderText", "Admin ID");
        pstn.putClientProperty("JTextField.placeholderText", "Position/College");
        adfname.putClientProperty("JTextField.placeholderText", "First Name");
        admname.putClientProperty("JTextField.placeholderText", "Middle Name");
        adlname.putClientProperty("JTextField.placeholderText", "Last Name");
        nmbr.putClientProperty("JTextField.placeholderText", "Contact Number");
        ml.putClientProperty("JTextField.placeholderText", "Email");
        sx.putClientProperty("JComboBox.placeholderText", "Sex");
        bdy.putClientProperty("JDateChooser.placeholderText", "Birthday");
        usrnm.putClientProperty("JTextField.placeholderText", "Userame");
        psswrd.putClientProperty("JTextField.placeholderText", "Password");
        cnfrm.putClientProperty("JTextField.placeholderText", "Confirm Password");

        jLabelimage.setText("");
        jLabelfinger.setText("");
        chssmg.setFont(RegisterFont.getFont("nstr", 14));
        scn.setFont(RegisterFont.getFont("nstr", 14));
        pdt.setFont(RegisterFont.getFont("nstr", 14));
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
        admin_id = new javax.swing.JTextField();
        pstn = new javax.swing.JTextField();
        jLabelimage = new javax.swing.JLabel();
        pdt = new javax.swing.JButton();
        scn = new javax.swing.JButton();
        clr = new javax.swing.JButton();
        nmbr = new javax.swing.JTextField();
        jLabelfinger = new javax.swing.JLabel();
        chssmg = new javax.swing.JButton();
        sx = new javax.swing.JComboBox<>();
        bdy = new com.toedter.calendar.JDateChooser();
        usrnm = new javax.swing.JTextField();
        psswrd = new javax.swing.JTextField();
        cnfrm = new javax.swing.JTextField();
        ml = new javax.swing.JTextField();

        jPanel1.setBackground(new java.awt.Color(119, 141, 169));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(13, 27, 42), 2, true), "Edit Admin", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.TOP, new java.awt.Font("SansSerif", 1, 36))); // NOI18N

        adlname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Personal Infromatiom:");

        adfname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        admname.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("University Information");

        admin_id.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        pstn.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        jLabelimage.setText("jLabel3");

        pdt.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        pdt.setText("update");

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

        usrnm.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        psswrd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        cnfrm.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        ml.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(admname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 334, Short.MAX_VALUE)
                            .addComponent(sx, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(adlname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(nmbr, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ml, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(bdy, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(usrnm, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(psswrd, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cnfrm, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(admin_id, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(scn, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                    .addComponent(jLabelfinger, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabelimage, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(chssmg, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(adfname, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pstn))
                        .addGap(31, 31, 31))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(112, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pdt, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(clr, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(97, 97, 97))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 26, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(admin_id, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(pstn, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 28, Short.MAX_VALUE)
                .addGap(0, 0, 0)
                .addComponent(adfname, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(admname, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(adlname, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nmbr, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(ml, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(sx, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bdy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(usrnm, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(psswrd, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(cnfrm, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelfinger, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelimage, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scn)
                    .addComponent(chssmg))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pdt)
                .addGap(4, 4, 4)
                .addComponent(clr, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void clrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clrActionPerformed
        admin_id.setText("");
        pstn.setText("");
        adfname.setText("");
        admname.setText("");
        adlname.setText("");
        nmbr.setText("");
        ml.setText("");
        sx.setSelectedIndex(-1);
        usrnm.setText("");
        psswrd.setText("");
        cnfrm.setText("");
        jLabelimage.setText("");
        jLabelfinger.setText("");
    }//GEN-LAST:event_clrActionPerformed
    public void buttonListener(ActionListener a) {
        pdt.addActionListener(a);
        scn.addActionListener(a);
        chssmg.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField adfname;
    public javax.swing.JTextField adlname;
    public javax.swing.JTextField admin_id;
    public javax.swing.JTextField admname;
    private com.toedter.calendar.JDateChooser bdy;
    public javax.swing.JButton chssmg;
    public javax.swing.JButton clr;
    public javax.swing.JTextField cnfrm;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    public javax.swing.JLabel jLabelfinger;
    public javax.swing.JLabel jLabelimage;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JTextField ml;
    public javax.swing.JTextField nmbr;
    public javax.swing.JButton pdt;
    public javax.swing.JTextField psswrd;
    public javax.swing.JTextField pstn;
    public javax.swing.JButton scn;
    public javax.swing.JComboBox<String> sx;
    public javax.swing.JTextField usrnm;
    // End of variables declaration//GEN-END:variables
}
