package org.example.java_fx_szoftverleltar;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class IrController {

    @FXML
    private ComboBox<String> tableSelect;

    @FXML
    private VBox dynamicFields;

    private TextField helyField; // "gep" tábla mezői
    private TextField tipusField;
    private TextField ipcimField;

    private TextField nevField; // "szoftver" tábla mezői
    private TextField kategoriaField;

    private TextField ipAddressField; // "telepites" tábla mezői
    private TextField softwareNameField;
    private TextField versionField;
    private TextField dateField;

    @FXML
    public void initialize() {
        // Táblázatok feltöltése
        tableSelect.getItems().addAll("gep", "szoftver", "telepites");

        // Ha tábla van kiválasztva, frissítsük az űrlapot
        tableSelect.setOnAction(event -> updateFormFields());
    }

    private void updateFormFields() {
        String selectedTable = tableSelect.getValue();
        if (selectedTable == null) {
            return;
        }

        dynamicFields.getChildren().clear();

        switch (selectedTable) {
            case "gep" -> setupGepFields();
            case "szoftver" -> setupSzoftverFields();
            case "telepites" -> setupTelepitesFields();
        }
    }

    private void setupGepFields() {
        helyField = addField("Hely", "Pl. T1-101");
        tipusField = addField("Típus", "Pl. asztali vagy notebook");
        ipcimField = addField("IP-cím", "Pl. 192.168.0.1");
    }

    private void setupSzoftverFields() {
        nevField = addField("Név", "Pl. Microsoft Word");
        kategoriaField = addField("Kategória", "Pl. irodai");
    }

    private void setupTelepitesFields() {
        ipAddressField = addField("Gép IP-cím", "Pl. 192.168.0.1");
        softwareNameField = addField("Szoftver neve", "Pl. Microsoft Word");
        versionField = addField("Verzió", "Pl. 1.0.0");
        dateField = addField("Dátum", "YYYY-MM-DD");
    }

    private TextField addField(String labelText, String promptText) {
        Label label = new Label(labelText);
        TextField textField = new TextField();
        textField.setPromptText(promptText);
        dynamicFields.getChildren().addAll(label, textField);
        return textField;
    }
    private boolean isValidDate(String date) {
        return date.matches("\\d{4}\\.\\d{2}\\.\\d{2}");
    }


    @FXML
    public void handleInsert() {
        String selectedTable = tableSelect.getValue();
        if (selectedTable == null) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Válassz egy táblát!");
            return;
        }

        switch (selectedTable) {
            case "gep" -> insertGep();
            case "szoftver" -> insertSzoftver();
            case "telepites" -> insertTelepites();
            default -> showAlert(Alert.AlertType.WARNING, "Hiba", "Ismeretlen tábla!");
        }
    }

    private void insertGep() {
        String hely = helyField.getText();
        String tipus = tipusField.getText();
        String ipcim = ipcimField.getText();

        if (hely.isEmpty() || tipus.isEmpty() || ipcim.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Minden mezőt ki kell tölteni!");
            return;
        }

        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO gep (hely, tipus, ipcim) VALUES (?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, hely);
                pstmt.setString(2, tipus);
                pstmt.setString(3, ipcim);
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Siker", "Rekord sikeresen hozzáadva!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az adatbázis művelet során.");
        }
    }

    private void insertSzoftver() {
        String nev = nevField.getText();
        String kategoria = kategoriaField.getText();

        if (nev.isEmpty() || kategoria.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Minden mezőt ki kell tölteni!");
            return;
        }

        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO szoftver (nev, kategoria) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, nev);
                pstmt.setString(2, kategoria);
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Siker", "Rekord sikeresen hozzáadva!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az adatbázis művelet során.");
        }
    }

    private void insertTelepites() {
        String ipAddress = ipAddressField.getText();
        String softwareName = softwareNameField.getText();
        String version = versionField.getText();
        String date = dateField.getText();

        if (ipAddress.isEmpty() || softwareName.isEmpty() || version.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Minden mezőt ki kell tölteni!");
            return;
        }

        // Dátum ellenőrzés
        if (!date.isEmpty() && !isValidDate(date)) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Helytelen dátumformátum! Használja az YYYY.MM.DD formátumot.");
            return;
        }

        try (Connection conn = Database.connect()) {
            int gepId = getIdByField(conn, "gep", "id", "ipcim", ipAddress);
            int szoftverId = getIdByField(conn, "szoftver", "id", "nev", softwareName);

            if (gepId == -1 || szoftverId == -1) {
                showAlert(Alert.AlertType.ERROR, "Hiba", "Érvénytelen adat!");
                return;
            }

            String query = "INSERT INTO telepites (gepid, szoftverid, verzio, datum) VALUES (?, ?, ?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setInt(1, gepId);
                pstmt.setInt(2, szoftverId);
                pstmt.setString(3, version);
                pstmt.setString(4, date.isEmpty() ? null : date); // Dátum közvetlen beszúrása
                pstmt.executeUpdate();
                showAlert(Alert.AlertType.INFORMATION, "Siker", "Rekord sikeresen hozzáadva!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt az adatbázis művelet során.");
        }
    }



    private int getIdByField(Connection conn, String tableName, String idColumn, String fieldName, String fieldValue) {
        String query = "SELECT " + idColumn + " FROM " + tableName + " WHERE " + fieldName + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, fieldValue);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(idColumn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Érvénytelen ID
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
