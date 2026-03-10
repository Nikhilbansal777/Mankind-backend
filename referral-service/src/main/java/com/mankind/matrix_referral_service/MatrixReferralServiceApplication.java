package com.mankind.matrix_referral_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@EnableFeignClients
@SpringBootApplication
public class MatrixReferralServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MatrixReferralServiceApplication.class, args);
    }
}
