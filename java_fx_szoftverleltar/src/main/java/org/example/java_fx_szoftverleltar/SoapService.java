package org.example.java_fx_szoftverleltar;

import org.example.mnb.*;
import javafx.collections.ObservableList;

import jakarta.xml.ws.WebServiceException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import javax.xml.namespace.QName;
import java.net.URL;

public class SoapService {

    private static final String WSDL_URL = "https://www.mnb.hu/arfolyamok.asmx?wsdl";
    private static final QName SERVICE_NAME = new QName("http://tempuri.org/", "MNBArfolyamServiceSoapImpl");

    public static MNBArfolyamServiceSoap getSoapPort() throws Exception {
        URL url = new URL(WSDL_URL);
        MNBArfolyamServiceSoapImpl service = new MNBArfolyamServiceSoapImpl(url, SERVICE_NAME);
        return service.getCustomBindingMNBArfolyamServiceSoap();
    }

    public static LocalDate getStartDateFromInterval() {
        try {
            MNBArfolyamServiceSoap port = getSoapPort();
            GetDateIntervalRequestBody requestBody = new GetDateIntervalRequestBody();
            GetDateIntervalResponseBody responseBody = port.getDateInterval(requestBody);

            String result = responseBody.getGetDateIntervalResult().getValue();
            if (result != null && !result.isEmpty()) {
                // Az XML feldolgozása megfelelően, hogy kiszedjük a kezdő dátumot
                int startIndex = result.indexOf("startdate=\"") + 11;
                int endIndex = result.indexOf("\"", startIndex);
                String startDateStr = result.substring(startIndex, endIndex);
                return LocalDate.parse(startDateStr.trim());
            }
        } catch (Exception e) {
            System.err.println("Hiba a dátumintervallum lekérésekor: " + e.getMessage());
        }
        return LocalDate.of(1949, 1, 3); // Alapértelmezett kezdődátum
    }


    public static void downloadAllData() {
        try {
            ObservableList<String> currencies = ChartHandler.getCurrencies();
            if (currencies.isEmpty()) {
                System.err.println("Nincs elérhető valuta a letöltéshez.");
                return;
            }

            String allCurrencies = String.join(",", currencies);

            String response = sendSOAPRequest(null, null, allCurrencies);
            Files.write(Paths.get("MNB_all_data.txt"), response.getBytes());
            System.out.println("Az összes adat sikeresen letöltve az MNB_all_data.txt fájlba!");

        } catch (Exception e) {
            System.err.println("Hiba az adatok letöltésekor: " + e.getMessage());
        }
    }

    public static String sendSOAPRequest(LocalDate startDate, LocalDate endDate, String currency) throws Exception {
        MNBArfolyamServiceSoap port = getSoapPort();

        LocalDate defaultStartDate = getStartDateFromInterval();
        String startDateValue = startDate != null ? startDate.toString() : defaultStartDate.toString();
        String endDateValue = endDate != null ? endDate.toString() : LocalDate.now().toString();

        GetExchangeRatesRequestBody requestBody = new GetExchangeRatesRequestBody();
        requestBody.setStartDate(new jakarta.xml.bind.JAXBElement<>(
                new QName("http://www.mnb.hu/webservices/", "startDate"), String.class, startDateValue));
        requestBody.setEndDate(new jakarta.xml.bind.JAXBElement<>(
                new QName("http://www.mnb.hu/webservices/", "endDate"), String.class, endDateValue));
        requestBody.setCurrencyNames(new jakarta.xml.bind.JAXBElement<>(
                new QName("http://www.mnb.hu/webservices/", "currencyNames"), String.class, currency));

        GetExchangeRatesResponseBody responseBody = port.getExchangeRates(requestBody);

        return responseBody.getGetExchangeRatesResult().getValue();
    }
}
