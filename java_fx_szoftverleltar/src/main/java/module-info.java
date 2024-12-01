module org.example.java_fx_szoftverleltar {
    requires javafx.controls;
    requires javafx.fxml;


    opens org.example.java_fx_szoftverleltar to javafx.fxml;
    exports org.example.java_fx_szoftverleltar;
}