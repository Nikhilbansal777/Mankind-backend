package com.mankind.notification_service.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    /**
     * Sends an SMS message to the specified phone number.
     * This is a simulated implementation. In a real-world scenario,
     * this would integrate with a third-party SMS gateway like Twilio.
     *
     * @param phoneNumber the recipient's phone number in E.164 format
     * @param message the content of the SMS message
     */
    public void sendSms(String phoneNumber, String message) {
        try {
            // Simulate API call to SMS gateway (e.g., Twilio)
            log.info("Simulating SMS gateway API call to send message to {}", phoneNumber);
            
            // In a real implementation, you would use a client library like:
            // Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
            // Message twilioMessage = Message.creator(
            //     new PhoneNumber(phoneNumber),
            //     new PhoneNumber(FROM_NUMBER),
            //     message
            // ).create();
            
            // Simulate successful SMS delivery
            log.info("SMS sent successfully to {}: {}", phoneNumber, message);
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            throw new RuntimeException("Failed to send SMS notification", e);
        }
    }
}