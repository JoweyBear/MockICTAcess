package Login;

import Utilities.RegisterFont;
import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import javax.swing.UIManager;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class LoginFrame extends javax.swing.JDialog {

    public LoginFrame() {
        initComponents();
        setLocationRelativeTo(null);
        jPanel1.putClientProperty(FlatClientProperties.STYLE, "arc: 50");
        srnm.putClientProperty("JTextField.placeholderText", "Type Username...");
        psswrd.putClientProperty("JTextField.placeholderText", "Type Password...");
        psswrd.putClientProperty(FlatClientProperties.STYLE, ""
                + "showRevealButton:true;");

        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
        UIManager.put("Panel.arc", 20);

        jLabel2.setFont(RegisterFont.getFont("vouge", 50f));
        cncl.setFont(RegisterFont.getFont("nstr", 14f));
        ntr.setFont(RegisterFont.getFont("nstr", 14f));
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
        srnm = new javax.swing.JTextField();
        psswrd = new javax.swing.JPasswordField();
        ntr = new javax.swing.JButton();
        cncl = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setUndecorated(true);

        jPanel1.setBackground(new java.awt.Color(65, 90, 119));
        jPanel1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        srnm.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        srnm.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(srnm, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 230, 290, 40));

        psswrd.setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N
        psswrd.setForeground(new java.awt.Color(255, 255, 255));
        jPanel1.add(psswrd, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 280, 290, 40));

        ntr.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        ntr.setText("Enter");
        jPanel1.add(ntr, new org.netbeans.lib.awtextra.AbsoluteConstraints(290, 330, 140, 30));

        cncl.setFont(new java.awt.Font("Instruction", 0, 14)); // NOI18N
        cncl.setText("Cancel");
        cncl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cnclActionPerformed(evt);
            }
        });
        jPanel1.add(cncl, new org.netbeans.lib.awtextra.AbsoluteConstraints(450, 330, 130, 30));

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
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cnclActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cnclActionPerformed
        dispose();
    }//GEN-LAST:event_cnclActionPerformed
    public void enterKeyListener(KeyListener evt) {
        psswrd.addKeyListener(evt);
    }

    public void buttonListener(ActionListener a) {
        ntr.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JButton cncl;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JButton ntr;
    public javax.swing.JPasswordField psswrd;
    public javax.swing.JTextField srnm;
    // End of variables declaration//GEN-END:variables
}
