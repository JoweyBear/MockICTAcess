package comboBoxHandlers;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.awt.EventQueue;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;

public class GooglePlacesCBHandler extends KeyAdapter {

    private final JComboBox<String> comboBox;
    private final List<String> list = new ArrayList<>();

    public GooglePlacesCBHandler(JComboBox<String> comboBox) {
        this.comboBox = comboBox;

    }

    @Override
    public void keyTyped(KeyEvent e) {
        fetchSuggestions(e);
    }

    private void fetchSuggestions(KeyEvent e) {
        EventQueue.invokeLater(() -> {
            String text = ((JTextField) e.getComponent()).getText();
            callGooglePlacesAPI(text);
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

    private void callGooglePlacesAPI(String query) {
        try {
            InputStream input = GooglePlacesCBHandler.class.getResourceAsStream("/Configurations/Outlet.properties");
            Properties outlet = new Properties();
            outlet.load(input);
            String urlStr = "https://maps.googleapis.com/maps/api/place/autocomplete/json?input="
                    + URLEncoder.encode(query, "UTF-8")
                    + "&key=" + outlet.getProperty("apiKey");

            HttpURLConnection conn = (HttpURLConnection) new URL(urlStr).openConnection();
            conn.setRequestMethod("GET");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JsonObject root = new Gson().fromJson(response.toString(), JsonObject.class);
                JsonArray predictions = root.getAsJsonArray("predictions");

                for (JsonElement element : predictions) {
                    JsonObject prediction = element.getAsJsonObject();
                    list.add(prediction.get("description").getAsString());

                    System.out.println(list);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(GooglePlacesCBHandler.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
