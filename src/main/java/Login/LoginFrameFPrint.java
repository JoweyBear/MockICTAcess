package Login;

import Utilities.RegisterFont;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class LoginFrameFPrint extends javax.swing.JDialog {

    public LoginFrameFPrint() {
        initComponents();
        setLocationRelativeTo(null);
//        srnm.putClientProperty("JTextField.placeholderText", "Type Username...");
//        psswrd.putClientProperty("JTextField.placeholderText", "Type Password...");
//        psswrd.putClientProperty(FlatClientProperties.STYLE, ""
//                + "showRevealButton:true;");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        UIManager.put("Panel.arc", 20);
        forPass.setBorderPainted(false);

        jLabel2.setFont(RegisterFont.getFont("vouge", 50f));
        cncl.setFont(RegisterFont.getFont("instr", 14f));
        lgn.setFont(RegisterFont.getFont("instr", 14f));

//        ntr.setBackground(new Color(62, 92, 118));
//        cncl.setBackground(new Color(62, 92, 118));
//        srnm.setBackground(new Color(0, 0, 0, 0));
//        srnm.putClientProperty("FlatLaf.style", "background:null");
//        psswrd.setBackground(new Color(0, 0, 0, 0));
//        psswrd.putClientProperty("FlatLaf.style", "background:null");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        cncl = new javax.swing.JButton();
        lgn = new javax.swing.JButton();
        forPass = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(65, 90, 119));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        cncl.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        cncl.setText("Cancel");
        cncl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cnclActionPerformed(evt);
            }
        });
        jPanel1.add(cncl, new org.netbeans.lib.awtextra.AbsoluteConstraints(540, 60, 90, -1));

        lgn.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        lgn.setText("Initiate Scan");
        lgn.setFocusPainted(false);
        lgn.setRequestFocusEnabled(false);
        lgn.setVerifyInputWhenFocusTarget(false);
        jPanel1.add(lgn, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 290, 210, 30));

        forPass.setText("Use Password Instead");
        jPanel1.add(forPass, new org.netbeans.lib.awtextra.AbsoluteConstraints(320, 350, 210, -1));

        jLabel2.setFont(new java.awt.Font("Vogue", 1, 50)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel2.setText("Welcome!");
        jPanel1.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 180, 290, 50));

        jLabel3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/TtF2.png"))); // NOI18N
        jPanel1.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(390, 90, -1, -1));

        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/Images/Grdnt1.png"))); // NOI18N
        jPanel1.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(210, 50, 427, 352));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 456, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cnclActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cnclActionPerformed
        dispose();
    }//GEN-LAST:event_cnclActionPerformed
//    public void enterKeyListener(KeyListener evt) {
//        psswrd.addKeyListener(evt);
//    }
//

    public void buttonListener(ActionListener a) {
        lgn.addActionListener(a);
        forPass.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton cncl;
    public javax.swing.JButton forPass;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JButton lgn;
    // End of variables declaration//GEN-END:variables
}
