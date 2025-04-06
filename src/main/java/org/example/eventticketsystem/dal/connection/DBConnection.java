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
        }
    }

    public static synchronized DBConnection getInstance() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.getConnection().isClosed()) {
            instance = new DBConnection();
        }
        return instance;
    }

    public static int getActiveConnections() {
        try (Connection conn = getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT COUNT(*) FROM sys.dm_exec_sessions WHERE status = 'running' AND is_user_process = 1");
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            logger.error("❌ Could not get active connections", e);
        }
        return -1;
    }

    public Connection getConnection() {
        return connection;
    }
}
