package org.example.java_fx_szoftverleltar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:C:/adatok/adatok.db";

    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.out.println("Kapcsolódási hiba: " + e.getMessage());
            return null;
        }
    }
    public static void main(String[] args) {
        Connection conn = Database.connect();
        if (conn != null) {
            System.out.println("Kapcsolódás sikeres!");
        } else {
            System.out.println("Kapcsolódási hiba!");
        }
    }

}
