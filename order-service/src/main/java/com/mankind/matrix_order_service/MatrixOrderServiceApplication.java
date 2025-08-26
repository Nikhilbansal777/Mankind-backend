package com.mankind.matrix_order_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.context.ConfigurableApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.mankind.matrix_order_service.client")
@EnableJpaAuditing
public class MatrixOrderServiceApplication {

    private static final Logger log = LoggerFactory.getLogger(MatrixOrderServiceApplication.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(MatrixOrderServiceApplication.class, args);
        
        // Add shutdown hook for graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Shutdown hook triggered, closing application context...");
            context.close();
            log.info("Application context closed successfully");
        }));
    }
}
