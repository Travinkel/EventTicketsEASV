package org.example.eventticketsystem.dal;

import org.example.eventticketsystem.utils.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        String url = Config.get("db.url");
        String user = Config.get("db.username");
        String pass = Config.get("db.password");

        try {
            connection = DriverManager.getConnection(url, user, pass);
            System.out.println("✅ Connected to the database successfully!");
        } catch (SQLException ex) {
            System.err.println("❌ Failed to connect to database:");
            ex.printStackTrace();
        }
    }

    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
