package org.example.java_fx_szoftverleltar;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Alert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class DeleteController {

    @FXML
    private ComboBox<String> tableSelect;

    @FXML
    private ComboBox<String> idSelect;

    @FXML
    public void initialize() {
        // Táblázatok feltöltése
        tableSelect.getItems().addAll("gep", "szoftver", "telepites");

        // Ha kiválasztják a táblát, töltsük be az ID-kat
        tableSelect.setOnAction(event -> loadIds());
    }

    private void loadIds() {
        String selectedTable = tableSelect.getValue();
        if (selectedTable == null) {
            return;
        }

        idSelect.getItems().clear();

        String query = "SELECT id FROM " + selectedTable;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                idSelect.getItems().add(rs.getString("id"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az ID-k betöltésekor.");
        }
    }

    @FXML
    public void handleDelete() {
        String selectedTable = tableSelect.getValue();
        String selectedId = idSelect.getValue();

        if (selectedTable == null || selectedId == null) {
            showAlert(Alert.AlertType.ERROR, "Hiányzó adatok", "Minden mezőt ki kell tölteni!");
            return;
        }

        String query = "DELETE FROM " + selectedTable + " WHERE id = ?";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, selectedId);
            pstmt.executeUpdate();
            showAlert(Alert.AlertType.INFORMATION, "Siker", "Rekord sikeresen törölve!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az adatbázis művelet során.");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
