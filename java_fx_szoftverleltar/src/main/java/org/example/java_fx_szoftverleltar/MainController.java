package org.example.java_fx_szoftverleltar;
import jakarta.xml.bind.JAXBElement;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.example.mnb.*;

import javax.xml.namespace.QName;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

public class MainController {

    @FXML
    public void showGepek() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/gep.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gépek");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showSzures() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/szures.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Szűrés");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showIr() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/ir.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Új Rekord Írása");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showModosit() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/modosit.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Rekord Módosítása");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showDelete() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/delete.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Rekord Törlése");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void showGomb() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/java_fx_szoftverleltar/gomb.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Gombok");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleDownloadAll() {
        // Teljes letöltés logika
        try {
            // Szolgáltatás hívás létrehozása
            MNBArfolyamServiceSoapImpl service = new MNBArfolyamServiceSoapImpl();
            MNBArfolyamServiceSoap port = service.getCustomBindingMNBArfolyamServiceSoap();

            // Lekérés létrehozása és elküldése
            LocalDate startDate = SoapService.getStartDateFromInterval();
            LocalDate endDate = LocalDate.now();

            GetExchangeRatesRequestBody request = new GetExchangeRatesRequestBody();
            request.setStartDate(new JAXBElement<>(new QName("http://www.mnb.hu/webservices/", "startDate"), String.class, startDate.toString()));
            request.setEndDate(new JAXBElement<>(new QName("http://www.mnb.hu/webservices/", "endDate"), String.class, endDate.toString()));
            request.setCurrencyNames(new JAXBElement<>(new QName("http://www.mnb.hu/webservices/", "currencyNames"), String.class, "EUR,USD,GBP,CHF,CZK,DKK,HRK,HUF,PLN,RON,RUB,SEK"));

            GetExchangeRatesResponseBody response = port.getExchangeRates(request);

            JAXBElement<String> exchangeRateResult = response.getGetExchangeRatesResult();
            if (exchangeRateResult != null) {
                String data = exchangeRateResult.getValue();
                Files.write(Paths.get("MNBteljes.txt"), data.getBytes());
                showAlert(Alert.AlertType.INFORMATION, "Sikeres letöltés", "Teljes adatok letöltve az MNBteljes.txt fájlba.");
            } else {
                showAlert(Alert.AlertType.ERROR, "Sikertelen letöltés", "Nem sikerült adatokat lekérni.");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Hiba", "Hiba történt: " + e.getMessage());
        }
    }

    @FXML
    private void handleFilteredGraph() {
        // Szűrt/Grafikon logika
        FormHandler.setupDownloadForm();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
