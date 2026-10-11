package com.mankind.notification_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
public class MailConfig {

    // JavaMailSender is automatically configured by Spring Boot
    // based on the properties in application.yml
    // This class is included for documentation purposes and
    // can be extended if custom mail configuration is needed
}