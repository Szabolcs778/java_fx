package org.example.java_fx_szoftverleltar;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ModositController {

    @FXML
    private ComboBox<String> tableSelect;

    @FXML
    private ComboBox<String> idSelect;

    @FXML
    private VBox dynamicFields;

    private List<TextField> textFields = new ArrayList<>();

    @FXML
    public void initialize() {
        // Táblázatok feltöltése
        tableSelect.getItems().addAll("gep", "szoftver", "telepites");

        // Táblázat kiválasztásakor töltsük fel az ID-kat
        tableSelect.setOnAction(event -> loadIds());

        // ID kiválasztásakor töltsük fel az adatokat
        idSelect.setOnAction(event -> loadRecordData());
    }

    private void loadIds() {
        String selectedTable = tableSelect.getValue();
        if (selectedTable == null) {
            return;
        }

        idSelect.getItems().clear();
        dynamicFields.getChildren().clear();

        String query = "SELECT id FROM " + selectedTable;
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                idSelect.getItems().add(String.valueOf(rs.getInt("id")));
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az ID-k betöltésekor.");
        }
    }

    private void loadRecordData() {
        String selectedTable = tableSelect.getValue();
        String selectedId = idSelect.getValue();
        if (selectedTable == null || selectedId == null) {
            return;
        }

        dynamicFields.getChildren().clear();
        textFields.clear();

        String query = switch (selectedTable) {
            case "gep" -> "SELECT hely, tipus, ipcim FROM gep WHERE id = ?";
            case "szoftver" -> "SELECT nev, kategoria FROM szoftver WHERE id = ?";
            case "telepites" -> """
            SELECT g.ipcim, s.nev, t.verzio, t.datum
            FROM telepites t
            JOIN gep g ON t.gepid = g.id
            JOIN szoftver s ON t.szoftverid = s.id
            WHERE t.id = ?
        """;
            default -> null;
        };

        if (query == null) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Ismeretlen tábla.");
            return;
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, selectedId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (selectedTable.equals("telepites")) {
                    addField("Gép IP-cím", rs.getString("ipcim"));
                    addField("Szoftver neve", rs.getString("nev"));
                    addField("Verzió", rs.getString("verzio"));
                    addField("Dátum", rs.getString("datum"));
                } else if (selectedTable.equals("gep")) {
                    addField("Hely", rs.getString("hely"));
                    addField("Típus", rs.getString("tipus"));
                    addField("IP-cím", rs.getString("ipcim"));
                } else if (selectedTable.equals("szoftver")) {
                    addField("Név", rs.getString("nev"));
                    addField("Kategória", rs.getString("kategoria"));
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Hiba", "Nem található rekord az adott ID alapján.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az adatok betöltése során.");
        }
    }


    private void addField(String labelText, String value) {
        Label label = new Label(labelText);
        TextField textField = new TextField(value);
        dynamicFields.getChildren().addAll(label, textField);
        textFields.add(textField);
    }

    @FXML
    public void handleUpdate() {
        String selectedTable = tableSelect.getValue();
        String selectedId = idSelect.getValue();

        if (selectedTable == null || selectedId == null || textFields.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Minden mezőt ki kell tölteni!");
            return;
        }

        String query = switch (selectedTable) {
            case "gep" -> "UPDATE gep SET hely = ?, tipus = ?, ipcim = ? WHERE id = ?";
            case "szoftver" -> "UPDATE szoftver SET nev = ?, kategoria = ? WHERE id = ?";
            case "telepites" -> """
            UPDATE telepites
            SET gepid = (SELECT id FROM gep WHERE ipcim = ?),
                szoftverid = (SELECT id FROM szoftver WHERE nev = ?),
                verzio = ?, datum = ?
            WHERE id = ?
        """;
            default -> null;
        };

        if (query == null) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Ismeretlen tábla.");
            return;
        }

        try (Connection conn = Database.connect()) {
            // Ellenőrizd, hogy az ipcim és a szoftver neve érvényesek-e
            if (selectedTable.equals("telepites")) {
                String ipcim = textFields.get(0).getText();
                String szoftverNev = textFields.get(1).getText();

                if (!recordExists(conn, "gep", "ipcim", ipcim)) {
                    showAlert(Alert.AlertType.ERROR, "Hiba", "Érvénytelen IP-cím: " + ipcim);
                    return;
                }
                if (!recordExists(conn, "szoftver", "nev", szoftverNev)) {
                    showAlert(Alert.AlertType.ERROR, "Hiba", "Érvénytelen szoftver név: " + szoftverNev);
                    return;
                }
            }

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                for (int i = 0; i < textFields.size(); i++) {
                    pstmt.setString(i + 1, textFields.get(i).getText());
                }
                pstmt.setString(textFields.size() + 1, selectedId);
                pstmt.executeUpdate();

                showAlert(Alert.AlertType.INFORMATION, "Siker", "Rekord sikeresen módosítva!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt a rekord módosítása során.");
        }
    }

    // Ellenőrzi, hogy egy adott rekord létezik-e
    private boolean recordExists(Connection conn, String tableName, String columnName, String value) {
        String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, value);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
