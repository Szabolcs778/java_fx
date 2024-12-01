package org.example.java_fx_szoftverleltar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import org.example.mnb.*;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ChartHandler {

    public static ObservableList<String> getCurrencies() {
        ObservableList<String> currencies = FXCollections.observableArrayList();

        try {
            MNBArfolyamServiceSoap port = SoapService.getSoapPort();

            // Kérés létrehozása
            GetCurrenciesRequestBody requestBody = new GetCurrenciesRequestBody();

            // SOAP kérés
            GetCurrenciesResponseBody responseBody = port.getCurrencies(requestBody);

            // Válasz feldolgozása
            String result = responseBody.getGetCurrenciesResult().getValue();
            if (result != null && !result.isEmpty()) {
                // Az eredmény XML újraparszolása
                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                DocumentBuilder builder = factory.newDocumentBuilder();
                Document doc = builder.parse(new InputSource(new StringReader(result)));

                // Kinyerjük a valuták listáját
                NodeList currencyNodes = doc.getElementsByTagName("Curr");
                for (int i = 0; i < currencyNodes.getLength(); i++) {
                    currencies.add(currencyNodes.item(i).getTextContent());
                }
            }
        } catch (Exception e) {
            System.err.println("Hiba a valuták lekérésekor: " + e.getMessage());
        }

        return currencies;
    }

    public static void displayChart(String currency, String soapData) {
        Stage stage = new Stage();
        stage.setTitle(currency + " árfolyamok");

        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName(currency);

        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;

        try {
            // SOAP válasz XML feldolgozása
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new InputSource(new StringReader(soapData)));

            // Az árfolyamok feldolgozása
            NodeList dayNodes = doc.getElementsByTagName("Day");
            for (int i = 0; i < dayNodes.getLength(); i++) {
                Element dayElement = (Element) dayNodes.item(i);
                String date = dayElement.getAttribute("date");

                NodeList rateNodes = dayElement.getElementsByTagName("Rate");
                for (int j = 0; j < rateNodes.getLength(); j++) {
                    Element rateElement = (Element) rateNodes.item(j);
                    if (rateElement.getAttribute("curr").equals(currency)) {
                        String rateValue = rateElement.getTextContent().replace(",", ".");
                        double rate = Double.parseDouble(rateValue);
                        series.getData().add(new XYChart.Data<>(date, rate));

                        if (rate < minValue) {
                            minValue = rate;
                        }
                        if (rate > maxValue) {
                            maxValue = rate;
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Hiba az adatok feldolgozásakor: " + e.getMessage());
        }

        // Y tengely határainak beállítása
        yAxis.setLowerBound(minValue - 5);
        yAxis.setUpperBound(maxValue + 5);
        yAxis.setAutoRanging(false);

        lineChart.getData().add(series);

        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
