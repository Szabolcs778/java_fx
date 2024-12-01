package org.example.java_fx_szoftverleltar;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Database {
    private static final String URL = "jdbc:sqlite:C:/adatok/adatok.db";

    /**
     * Establishes a connection to the SQLite database.
     *
     * @return A Connection object if successful, or null if an error occurs.
     */
    public static Connection connect() {
        try {
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Kapcsolódási hiba: " + e.getMessage());
            return null;
        }
    }
}
