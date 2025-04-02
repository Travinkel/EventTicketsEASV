package org.example.eventticketsystem.dal;

import org.example.eventticketsystem.utils.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class.getName());

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() {
        String url = Config.get("db.url");
        String user = Config.get("db.username");
        String pass = Config.get("db.password");

        try {
            connection = DriverManager.getConnection(url, user, pass);
            logger.info("✅ Connected to the database successfully!");
        } catch (SQLException ex) {
            logger.error("❌ Failed to connect to database", ex);
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
