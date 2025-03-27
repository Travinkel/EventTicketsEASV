package org.example.eventticketsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private static final Object LOCK = new Object();
    private Connection connection;

    private final String url = "jdbc:sqlserver://10.176.111.34:1433;databaseName=EventTicketsEASV_33;encrypt=false;";
    private final String user = "CSe2024a_e_33";
    private final String password = "CSe2024aE33!24";

    private DBConnection() {
        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("✅ Connected to the database successfully!");
        } catch (SQLException e) {
            System.err.println("❌ Failed to connect to database:");
            e.printStackTrace();
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
