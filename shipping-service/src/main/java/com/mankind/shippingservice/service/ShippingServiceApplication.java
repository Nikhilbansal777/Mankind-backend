package com.mankind.shippingservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients; // add

@SpringBootApplication
@EnableFeignClients // add

public class ShippingServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShippingServiceApplication.class, args);
    }
}