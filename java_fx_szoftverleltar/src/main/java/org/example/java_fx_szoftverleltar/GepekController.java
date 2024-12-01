package org.example.java_fx_szoftverleltar;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class GepekController {

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

        gepHelyColumn.setCellValueFactory(new PropertyValueFactory<>("gepHely"));
        gepTipusColumn.setCellValueFactory(new PropertyValueFactory<>("gepTipus"));
        gepIpcimColumn.setCellValueFactory(new PropertyValueFactory<>("gepIpcim"));

        szoftverNevColumn.setCellValueFactory(new PropertyValueFactory<>("szoftverNev"));
        szoftverKategoriaColumn.setCellValueFactory(new PropertyValueFactory<>("szoftverKategoria"));
        telepitesVerzioColumn.setCellValueFactory(new PropertyValueFactory<>("telepitesVerzio"));
        telepitesDatumColumn.setCellValueFactory(new PropertyValueFactory<>("telepitesDatum"));
        loadTelepites();
    }

    @FXML
    public void loadTelepites() {
        telepitesList.clear();
        String query = "SELECT gep.id AS gepId, gep.hely AS gepHely, gep.tipus AS gepTipus, gep.ipcim AS gepIpcim, " +
                "szoftver.id AS szoftverId, szoftver.nev AS szoftverNev, szoftver.kategoria AS szoftverKategoria, " +
                "telepites.verzio AS telepitesVerzio, telepites.datum AS telepitesDatum " +
                "FROM telepites " +
                "INNER JOIN gep ON telepites.gepid = gep.id " +
                "INNER JOIN szoftver ON telepites.szoftverid = szoftver.id";

        try (Connection conn = Database.connect();
             PreparedStatement pstmt = conn.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {

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
