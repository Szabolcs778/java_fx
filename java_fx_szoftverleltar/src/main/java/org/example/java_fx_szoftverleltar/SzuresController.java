package org.example.java_fx_szoftverleltar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SzuresController {

    @FXML
    private TextField helyInput;

    @FXML
    private ComboBox<String> tipusInput;

    @FXML
    private TextField ipcimInput;

    @FXML
    private RadioButton datumVan;

    @FXML
    private RadioButton datumNincs;

    private ToggleGroup datumGroup;

    @FXML
    private ComboBox<String> szoftverInput;

    @FXML
    private TableView<TelepitesView> telepitesTable;

    @FXML
    private TableColumn<TelepitesView, Integer> gepIdColumn;
    @FXML
    private TableColumn<TelepitesView, String> gepHelyColumn;
    @FXML
    private TableColumn<TelepitesView, String> gepTipusColumn;
    @FXML
    private TableColumn<TelepitesView, String> gepIpcimColumn;
    @FXML
    private TableColumn<TelepitesView, Integer> szoftverIdColumn;
    @FXML
    private TableColumn<TelepitesView, String> szoftverNevColumn;
    @FXML
    private TableColumn<TelepitesView, String> szoftverKategoriaColumn;
    @FXML
    private TableColumn<TelepitesView, String> telepitesVerzioColumn;
    @FXML
    private TableColumn<TelepitesView, String> telepitesDatumColumn;

    private ObservableList<TelepitesView> telepitesList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        datumGroup = new ToggleGroup();
        datumVan.setToggleGroup(datumGroup);
        datumNincs.setToggleGroup(datumGroup);

        // Alapértelmezett választás beállítása (pl. "Nincs")
        datumNincs.setSelected(true);

        // Egyéb inicializálás
        loadSzoftverOptions();

        gepIdColumn.setCellValueFactory(new PropertyValueFactory<>("gepId"));
        gepHelyColumn.setCellValueFactory(new PropertyValueFactory<>("gepHely"));
        gepTipusColumn.setCellValueFactory(new PropertyValueFactory<>("gepTipus"));
        gepIpcimColumn.setCellValueFactory(new PropertyValueFactory<>("gepIpcim"));
        szoftverIdColumn.setCellValueFactory(new PropertyValueFactory<>("szoftverId"));
        szoftverNevColumn.setCellValueFactory(new PropertyValueFactory<>("szoftverNev"));
        szoftverKategoriaColumn.setCellValueFactory(new PropertyValueFactory<>("szoftverKategoria"));
        telepitesVerzioColumn.setCellValueFactory(new PropertyValueFactory<>("telepitesVerzio"));
        telepitesDatumColumn.setCellValueFactory(new PropertyValueFactory<>("telepitesDatum"));

        loadSzoftverOptions();
    }

    private void loadSzoftverOptions() {
        ObservableList<String> szoftverOptions = FXCollections.observableArrayList();
        szoftverOptions.add("Mind");
        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement("SELECT nev FROM szoftver");
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                szoftverOptions.add(rs.getString("nev"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        szoftverInput.setItems(szoftverOptions);
    }

    @FXML
    public void handleSzures() {
        telepitesList.clear();
        StringBuilder query = new StringBuilder(
                "SELECT gep.id AS gepId, gep.hely AS gepHely, gep.tipus AS gepTipus, gep.ipcim AS gepIpcim, " +
                        "szoftver.id AS szoftverId, szoftver.nev AS szoftverNev, szoftver.kategoria AS szoftverKategoria, " +
                        "telepites.verzio AS telepitesVerzio, telepites.datum AS telepitesDatum " +
                        "FROM telepites " +
                        "INNER JOIN gep ON telepites.gepid = gep.id " +
                        "INNER JOIN szoftver ON telepites.szoftverid = szoftver.id WHERE 1=1"
        );

        // Hely szűrés
        if (!helyInput.getText().isEmpty()) {
            query.append(" AND gep.hely LIKE ?");
        }

        // Típus szűrés
        if (tipusInput.getValue() != null && !tipusInput.getValue().equals("Mind")) {
            query.append(" AND gep.tipus = ?");
        }

        // IP cím szűrés
        if (!ipcimInput.getText().isEmpty()) {
            query.append(" AND gep.ipcim LIKE ?");
        }
        // Dátum szűrés
        // Dátum szűrés
        if (datumVan.isSelected()) {
            query.append(" AND telepites.datum IS NOT NULL");
        } else if (datumNincs.isSelected()) {
            query.append(" AND telepites.datum IS NULL");
        }


        // Szoftver szűrés
        if (szoftverInput.getValue() != null && !szoftverInput.getValue().equals("Mind")) {
            query.append(" AND szoftver.nev = ?");
        }

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;

            if (!helyInput.getText().isEmpty()) {
                pstmt.setString(paramIndex++, helyInput.getText() + "%");
            }
            if (tipusInput.getValue() != null && !tipusInput.getValue().equals("Mind")) {
                pstmt.setString(paramIndex++, tipusInput.getValue());
            }
            if (!ipcimInput.getText().isEmpty()) {
                pstmt.setString(paramIndex++, ipcimInput.getText() + "%");
            }
            if (szoftverInput.getValue() != null && !szoftverInput.getValue().equals("Mind")) {
                pstmt.setString(paramIndex++, szoftverInput.getValue());
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                telepitesList.add(new TelepitesView(
                        rs.getInt("gepId"),
                        rs.getString("gepHely"),
                        rs.getString("gepTipus"),
                        rs.getString("gepIpcim"),
                        rs.getInt("szoftverId"),
                        rs.getString("szoftverNev"),
                        rs.getString("szoftverKategoria"),
                        rs.getString("telepitesVerzio"),
                        rs.getString("telepitesDatum")
                ));
            }
            telepitesTable.setItems(telepitesList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
