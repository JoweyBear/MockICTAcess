package Utilities;

import Connection.Ticket;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class FacultyCBHandler extends KeyAdapter {

    private final JComboBox<String> comboBox;
    private final List<String> list = new ArrayList<>();
    private final Encryption de = new Encryption();

    public FacultyCBHandler(JComboBox<String> combo) {
        this.comboBox = combo;
        try (
                Connection conn = Ticket.getConn(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT user_id, fname, mname, lname FROM user WHERE role = 'faculty' AND is_active = 1")) {
            while (rs.next()) {
                String id = rs.getString("user_id");
                String fname = de.decrypt(rs.getString("fname"));
                String mname = de.decrypt(rs.getString("mname"));
                String lname = de.decrypt(rs.getString("lname"));

                String middleInitial = (mname != null && !mname.trim().isEmpty())
                        ? mname.trim().substring(0, 1).toUpperCase() + "."
                        : "";

                String item = String.format("Faculty ID: %s - %s %s %s", id, fname, middleInitial, lname);
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacultyCBHandler.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        updateSuggestions(e);
    }

    private void updateSuggestions(KeyEvent e) {
        EventQueue.invokeLater(() -> {
            String text = ((JTextField) e.getComponent()).getText();
            ComboBoxModel<String> model = getSuggestedModel(list, text);
            comboBox.setModel(model);
            comboBox.setSelectedIndex(-1);
            ((JTextField) comboBox.getEditor().getEditorComponent()).setText(text);
            if (model.getSize() > 0) {
                comboBox.showPopup();
            } else {
                comboBox.hidePopup();
            }
        });
    }

    private ComboBoxModel<String> getSuggestedModel(List<String> data, String input) {
        DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
        for (String s : data) {
            if (s.toLowerCase().contains(input.toLowerCase())) {
                model.addElement(s);
            }
        }
        return model;
    }
}
