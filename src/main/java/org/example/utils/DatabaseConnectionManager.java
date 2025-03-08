package org.example.utils;

import org.example.custom.exception.CustomException;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseConnectionManager {
    private String url;
    public static String user;
    private String password;

    public DatabaseConnectionManager() throws CustomException {
        try (FileInputStream file = new FileInputStream("src/main/resources/config.properties")) {
            Properties properties = new Properties();
            properties.load(file);

            this.url = properties.getProperty("db.url");
            user = properties.getProperty("db.user");
            this.password = properties.getProperty("db.password");

            if (url == null || user == null || password == null) {
                throw new CustomException("Error: Database credentials are missing in config file.");
            }
        } catch (IOException ioException) {
            throw new CustomException("Error loading configuration: " + ioException.getMessage());
        }
    }

    public Connection getConnection() throws CustomException {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException sqlException) {
            throw new CustomException("Error: " + sqlException.getMessage());
        }
    }
}