package Utilities;

import Connection.Ticket;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

public class CSCBHandler extends KeyAdapter {

    private final JComboBox<String> comboBox;
    private final List<String> list = new ArrayList<>();

    public CSCBHandler(JComboBox<String> combo) {
        this.comboBox = combo;
        try (Connection conn = Ticket.getConn(); 
                Statement stmt = conn.createStatement(); 
                ResultSet rs = stmt.executeQuery("SELECT cs_id, subject FROM class_schedule")) {
            while (rs.next()) {
                String item = String.format("Class Sched. ID: %s - %s", rs.getString("cs_id"), rs.getString("subject"));
                list.add(item);
            }
        } catch (SQLException ex) {
            Logger.getLogger(RoomCBHandler.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
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
