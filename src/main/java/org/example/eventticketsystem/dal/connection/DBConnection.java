package org.example.eventticketsystem.dal.connection;

import org.example.eventticketsystem.utils.Config;

import java.sql.*;

import org.example.eventticketsystem.utils.di.Injectable;
import org.example.eventticketsystem.utils.di.Scope;
import org.example.eventticketsystem.utils.di.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The DBConnection class is responsible for managing the database connection.
 * It has been refactored to align with the principles of dependency injection (DI)
 * and to allow the DI framework to manage its lifecycle.
 */

// Indicates that this class should be treated as a singleton
@Singleton
// Explicitly defines the scope as "singleton" for clarity and extensibility.
@Scope("singleton")
@Injectable
public class DBConnection {
    private static final Logger LOGGER = LoggerFactory.getLogger(DBConnection.class.getName());
    private final Config config;

    /**
     * Constructor is public to allow the DI framework to instantiate this class.
     * The DI framework will ensure that only one instance is created due to the @Singleton annotation.
     */
    public DBConnection(Config config) throws SQLException {
        this.config = config;
    }

    /**
     * Provides access to the database connection.
     * The DI framework ensures that the same instance of DBConnection is used wherever injected.
     */
    public Connection getConnection() throws SQLException {
        String url = config.get(Config.Key.DB_URL);
        String user = config.get(Config.Key.DB_USERNAME);
        String password = config.get(Config.Key.DB_PASSWORD);

        return DriverManager.getConnection(url, user, password);
    }
}
