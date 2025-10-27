package com.mankind.matrix_wishlistservice;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class MatrixWishlishServiceApplicationTests {

    static {
        // Load environment variables from parent directory .env file
        Dotenv dotenv = Dotenv.configure()
            .directory("../")
            .load();
        
        // Set system properties for Spring Boot
        System.setProperty("DB_HOST", dotenv.get("DB_HOST"));
        System.setProperty("DB_PORT", dotenv.get("DB_PORT"));
        System.setProperty("DB_NAME", dotenv.get("DB_NAME"));
        System.setProperty("DB_USERNAME", dotenv.get("DB_USERNAME"));
        System.setProperty("DB_PASSWORD", dotenv.get("DB_PASSWORD"));
        System.setProperty("DB_CONNECT_TIMEOUT", dotenv.get("DB_CONNECT_TIMEOUT"));
        System.setProperty("DB_SOCKET_TIMEOUT", dotenv.get("DB_SOCKET_TIMEOUT"));
        System.setProperty("DB_USE_SSL", dotenv.get("DB_USE_SSL"));
        System.setProperty("DB_ALLOW_PUBLIC_KEY_RETRIEVAL", dotenv.get("DB_ALLOW_PUBLIC_KEY_RETRIEVAL"));
        System.setProperty("DB_SERVER_TIMEZONE", dotenv.get("DB_SERVER_TIMEZONE"));
        System.setProperty("DB_AUTO_RECONNECT", dotenv.get("DB_AUTO_RECONNECT"));
        System.setProperty("DB_FAIL_OVER_READ_ONLY", dotenv.get("DB_FAIL_OVER_READ_ONLY"));
        System.setProperty("DB_HIKARI_CONNECTION_TIMEOUT", dotenv.get("DB_HIKARI_CONNECTION_TIMEOUT"));
        System.setProperty("DB_HIKARI_MAX_POOL_SIZE", dotenv.get("DB_HIKARI_MAX_POOL_SIZE"));
        System.setProperty("DB_HIKARI_MIN_IDLE", dotenv.get("DB_HIKARI_MIN_IDLE"));
        System.setProperty("DB_HIKARI_IDLE_TIMEOUT", dotenv.get("DB_HIKARI_IDLE_TIMEOUT"));
        System.setProperty("DB_HIKARI_MAX_LIFETIME", dotenv.get("DB_HIKARI_MAX_LIFETIME"));
        System.setProperty("KEYCLOAK_URL", dotenv.get("KEYCLOAK_URL"));
        System.setProperty("PRODUCT_SERVICE_URL", dotenv.get("PRODUCT_SERVICE_URL"));
    }

	@Test
	void contextLoads() {
	}

}
