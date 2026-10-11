package com.mankind.matrix_product_service;

import java.sql.Connection;
import java.sql.DriverManager;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import io.github.cdimascio.dotenv.Dotenv;

public class ConnectionTest {
    
    @Test
    public void testDatabaseConnection() {
        // Load environment variables from .env file
        Dotenv dotenv = Dotenv.load();

        // Get database connection details from environment variables
        String url = String.format("jdbc:mysql://%s:%s/%s?connectTimeout=%s&socketTimeout=%s&useSSL=%s&allowPublicKeyRetrieval=%s&serverTimezone=%s&autoReconnect=%s&failOverReadOnly=%s",
            dotenv.get("DB_HOST"),
            dotenv.get("DB_PORT"),
            dotenv.get("DB_NAME"),
            dotenv.get("DB_CONNECT_TIMEOUT"),
            dotenv.get("DB_SOCKET_TIMEOUT"),
            dotenv.get("DB_USE_SSL"),
            dotenv.get("DB_ALLOW_PUBLIC_KEY_RETRIEVAL"),
            dotenv.get("DB_SERVER_TIMEZONE"),
            dotenv.get("DB_AUTO_RECONNECT"),
            dotenv.get("DB_FAIL_OVER_READ_ONLY")
        );
        String username = dotenv.get("DB_USERNAME");
        String password = dotenv.get("DB_PASSWORD");

        try {
            System.out.println("Testing database connection...");
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Connection successful!");
            assertTrue(conn.isValid(5), "Connection should be valid");
            conn.close();
        } catch (Exception e) {
            System.out.println("Connection failed!");
            e.printStackTrace();
            fail("Database connection test failed: " + e.getMessage());
        }
    }
} 