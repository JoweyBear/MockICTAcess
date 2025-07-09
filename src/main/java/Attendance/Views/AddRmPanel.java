package Attendance.Views;

import Utilities.RegisterFont;
import java.awt.Color;
import java.awt.event.ActionListener;
import javax.swing.UIManager;

public class AddRmPanel extends javax.swing.JPanel {

    public AddRmPanel() {
        UIManager.put("TextField.background", Color.white);
        initComponents();

        bldng.putClientProperty("JTextField.placeholderText", "Building");
        rmNm.putClientProperty("JTextField.placeholderText", "Room Name");
        dscp.putClientProperty("JTextField.placeholderText", "Description/Remarks");


        dd.setFont(RegisterFont.getFont("nstr", 14));
        clr.setFont(RegisterFont.getFont("nstr", 14));
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        bldng = new javax.swing.JTextField();
        cllg = new javax.swing.JComboBox<>();
        dd = new javax.swing.JButton();
        clr = new javax.swing.JButton();
        rmNm = new javax.swing.JTextField();
        flrLvl = new javax.swing.JComboBox<>();
        typ = new javax.swing.JComboBox<>();
        dscp = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(264, 215));

        jPanel1.setBackground(new java.awt.Color(119, 141, 169));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Add Room", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("SansSerif", 1, 18))); // NOI18N

        bldng.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        cllg.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        cllg.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "College", "CICT", "CIT", "CHM", "COED" }));

        dd.setText("Add");

        clr.setText("Clear");
        clr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clrActionPerformed(evt);
            }
        });

        rmNm.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        flrLvl.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        flrLvl.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Floor Level", "1st", "2nd", "3rd" }));

        typ.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        typ.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Type of Room", "Classrom", "Laboratory", "Office" }));

        dscp.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(dd, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE)
                        .addGap(63, 63, 63)
                        .addComponent(clr, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))
                    .addComponent(bldng)
                    .addComponent(cllg, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(rmNm)
                    .addComponent(flrLvl, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(typ, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dscp, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(cllg, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bldng, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rmNm, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(flrLvl, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(typ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dscp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dd)
                    .addComponent(clr))
                .addGap(21, 21, 21))
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

    private void clrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clrActionPerformed
        bldng.setText("");
        cllg.setSelectedIndex(0);
        dscp.setText("");
        flrLvl.setSelectedIndex(0);
        rmNm.setText("");
        typ.setSelectedIndex(0);
    }//GEN-LAST:event_clrActionPerformed
    public void buttonListener(ActionListener a){
        dd.addActionListener(a);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    public javax.swing.JTextField bldng;
    public javax.swing.JComboBox<String> cllg;
    public javax.swing.JButton clr;
    public javax.swing.JButton dd;
    public javax.swing.JTextField dscp;
    public javax.swing.JComboBox<String> flrLvl;
    private javax.swing.JPanel jPanel1;
    public javax.swing.JTextField rmNm;
    public javax.swing.JComboBox<String> typ;
    // End of variables declaration//GEN-END:variables
}
