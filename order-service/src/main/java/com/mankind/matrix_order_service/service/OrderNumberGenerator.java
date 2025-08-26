package com.mankind.matrix_order_service.service;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
public class OrderNumberGenerator {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HHmmss");
    private static final Random RANDOM = new Random();
    
    public String generateOrderNumber() {
        LocalDateTime now = LocalDateTime.now();
        String date = now.format(DATE_FORMATTER);
        String time = now.format(TIME_FORMATTER);
        String random = String.format("%05d", RANDOM.nextInt(100000));
        
        return String.format("ORD-%s-%s-%s", date, time, random);
    }
}
