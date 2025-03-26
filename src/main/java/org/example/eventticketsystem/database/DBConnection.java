package org.example.eventticketsystem.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
        private static DBConnection instance;
        private Connection connection;

        private final String url = "jdbc:sqlserver://localhost:1433;databaseName=EventTicketsDB;encrypt=false";
        private final String user = "your_username";
        private final String password = "your_password";

        private DBConnection() {
            try {
                connection = DriverManager.getConnection(url, user, password);
                System.out.println("Database connected!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        public static DBConnection getInstance() {
            if (instance == null) {
                instance = new DBConnection();
            }
            return instance;
        }

        public Connection getConnection() {
            return connection;
        }
    }
