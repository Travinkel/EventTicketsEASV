package org.example.eventticketsystem.dal.connection;

import org.example.eventticketsystem.utils.Config;

import java.sql.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DBConnection {
    private static final Logger logger = LoggerFactory.getLogger(DBConnection.class.getName());

    private static DBConnection instance;
    private Connection connection;

    private DBConnection() throws SQLException {
        String url = Config.get("db.url");
        String user = Config.get("db.username");
        String pass = Config.get("db.password");

        try {
            connection = DriverManager.getConnection(url, user, pass);
            logger.info("✅ Connected to the database successfully!");
        } catch (SQLException ex) {
            logger.error("❌ Failed to connect to database", ex);
            throw ex;
        }
    }


    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnection();  // Recreate instance if connection is closed
        }
        return instance;
    }


    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(Config.get("db.url"), Config.get("db.username"), Config.get("db.password"));
        }
        return connection;
    }
}
