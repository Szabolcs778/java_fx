module org.example.java_fx_szoftverleltar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires jakarta.xml.ws;
    requires jakarta.jws;
    requires jakarta.activation;
    requires jakarta.xml.bind;

    opens org.example.java_fx_szoftverleltar to javafx.fxml, jakarta.xml.bind;
    opens org.example.mnb to jakarta.xml.bind, jakarta.xml.ws, com.sun.xml.bind;


    exports org.example.java_fx_szoftverleltar;
    exports org.example.mnb;
}
