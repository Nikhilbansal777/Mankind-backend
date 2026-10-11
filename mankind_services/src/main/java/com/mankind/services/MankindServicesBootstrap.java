package com.mankind.services;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(
        basePackages = "com.mankind",
        excludeFilters = @ComponentScan.Filter(
                type = FilterType.REGEX,
                pattern = "com\\.mankind\\..*Application$"
        )
)
@EnableFeignClients(basePackages = "com.mankind")
@EnableJpaAuditing
@EntityScan(basePackages = "com.mankind")
@EnableJpaRepositories(basePackages = "com.mankind")
public class MankindServicesBootstrap {

    public static void main(String[] args) {
        SpringApplication.run(MankindServicesBootstrap.class, args);
    }
}
