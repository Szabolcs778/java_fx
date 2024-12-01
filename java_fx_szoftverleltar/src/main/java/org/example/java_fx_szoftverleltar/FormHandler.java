package org.example.java_fx_szoftverleltar;

import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class FormHandler {

    public static void setupDownloadForm() {
        VBox formLayout = new VBox(10);

        // Dátumválasztók
        DatePicker startDatePicker = new DatePicker();
        DatePicker endDatePicker = new DatePicker();

        // Valuták ComboBox
        ComboBox<String> currencyComboBox = new ComboBox<>();
        ObservableList<String> currencies = ChartHandler.getCurrencies(); // Lekérjük az elérhető valutákat
        currencyComboBox.setItems(currencies);

        // Letöltés gomb
        Button downloadButton = new Button("Letöltés");
        downloadButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String currency = currencyComboBox.getValue();

            try {
                if (startDate == null || endDate == null || currency == null) {
                    showAlert(Alert.AlertType.ERROR, "Hiányzó adatok", "Kérjük, töltsd ki az összes mezőt!");
                    return;
                }

                System.out.println("Start Date: " + startDate);
                System.out.println("End Date: " + endDate);
                System.out.println("Currency: " + currency);

                // SOAP Kérés
                String data = SoapService.sendSOAPRequest(startDate, endDate, currency);
                Files.write(Paths.get("MNBsz.txt"), data.getBytes());
                showAlert(Alert.AlertType.INFORMATION, "Sikeres letöltés", "Kiválasztott adatok letöltve az MNBsz.txt fájlba.");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba az adatok letöltésekor: " + e.getMessage());
            }
        });

        // Grafikon gomb
        Button chartButton = new Button("Grafikon");
        chartButton.setOnAction(event -> {
            LocalDate startDate = startDatePicker.getValue();
            LocalDate endDate = endDatePicker.getValue();
            String currency = currencyComboBox.getValue();

            try {
                if (startDate == null || endDate == null || currency == null) {
                    showAlert(Alert.AlertType.ERROR, "Hiányzó adatok", "Kérjük, töltsd ki az összes mezőt!");
                    return;
                }

                // SOAP Kérés
                String data = SoapService.sendSOAPRequest(startDate, endDate, currency);

                // Adatok átadása a ChartHandler-nek feldolgozásra
                ChartHandler.displayChart(currency, data);
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba a grafikon megjelenítésekor: " + e.getMessage());
            }
        });

        // Form elemek hozzáadása
        formLayout.getChildren().addAll(
                new Label("Kezdő dátum:"), startDatePicker,
                new Label("Végdátum:"), endDatePicker,
                new Label("Deviza:"), currencyComboBox,
                downloadButton,
                chartButton // Grafikon gomb hozzáadva
        );

        // Űrlap megjelenítése
        Scene scene = new Scene(formLayout, 400, 300);
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.setTitle("Letöltés űrlap");
        stage.show();
    }

    private static void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
