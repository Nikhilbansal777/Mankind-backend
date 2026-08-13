package com.mankind.notification_service.service;

import com.mankind.notification_service.model.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private SmsNotificationService smsNotificationService;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest request(
        String userEmail,
        String subject,
        String message,
        String type
) {
    NotificationRequest request = mock(NotificationRequest.class);

    lenient().when(request.getUserEmail()).thenReturn(userEmail);
    lenient().when(request.getSubject()).thenReturn(subject);
    lenient().when(request.getMessage()).thenReturn(message);
    lenient().when(request.getType()).thenReturn(type);

    return request;
}

    @Test
    void sendNotification_shouldSendEmail_whenTypeIsEmail() {
        NotificationRequest request = request(
                "user@example.com",
                "Order Confirmation",
                "Your order has been confirmed.",
                "EMAIL"
        );

        notificationService.sendNotification(request);

        ArgumentCaptor<SimpleMailMessage> captor =
                ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        verifyNoInteractions(smsNotificationService);

        SimpleMailMessage sent = captor.getValue();
        assertArrayEquals(new String[]{"user@example.com"}, sent.getTo());
        assertEquals("Order Confirmation", sent.getSubject());
        assertEquals("Your order has been confirmed.", sent.getText());
    }

    @Test
    void sendNotification_shouldHandleEmailTypeIgnoringCase() {
        NotificationRequest request = request(
                "user@example.com",
                "Welcome",
                "Welcome to Mankind Matrix.",
                "email"
        );

        notificationService.sendNotification(request);

        verify(mailSender).send(any(SimpleMailMessage.class));
        verifyNoInteractions(smsNotificationService);
    }

    @Test
    void sendNotification_shouldWrapException_whenEmailSendingFails() {
        NotificationRequest request = request(
                "user@example.com",
                "Order Confirmation",
                "Your order has been confirmed.",
                "EMAIL"
        );
        RuntimeException cause = new RuntimeException("SMTP server unavailable");
        doThrow(cause).when(mailSender).send(any(SimpleMailMessage.class));

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> notificationService.sendNotification(request)
        );

        assertEquals("Failed to send email notification", thrown.getMessage());
        assertSame(cause, thrown.getCause());
        verifyNoInteractions(smsNotificationService);
    }

    @Test
    void sendNotification_shouldSendSms_whenTypeIsSms() {
        NotificationRequest request = request(
                "+13125551234",
                "Verification",
                "Your verification code is 123456.",
                "SMS"
        );

        notificationService.sendNotification(request);

        verify(smsNotificationService).sendSms(
                "+13125551234",
                "Your verification code is 123456."
        );
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendNotification_shouldHandleSmsTypeIgnoringCase() {
        NotificationRequest request = request(
                "+13125551234",
                "Verification",
                "Your verification code is 123456.",
                "sms"
        );

        notificationService.sendNotification(request);

        verify(smsNotificationService).sendSms(
                "+13125551234",
                "Your verification code is 123456."
        );
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendNotification_shouldPropagateException_whenSmsSendingFails() {
        NotificationRequest request = request(
                "+13125551234",
                "Verification",
                "Your verification code is 123456.",
                "SMS"
        );
        RuntimeException cause = new RuntimeException("SMS gateway unavailable");
        doThrow(cause).when(smsNotificationService).sendSms(
                "+13125551234",
                "Your verification code is 123456."
        );

        RuntimeException thrown = assertThrows(
                RuntimeException.class,
                () -> notificationService.sendNotification(request)
        );

        assertSame(cause, thrown);
        verifyNoInteractions(mailSender);
    }

    @Test
    void sendNotification_shouldIgnoreInAppType() {
        NotificationRequest request = request(
                "user@example.com",
                "In-App Notification",
                "You have a new message.",
                "INAPP"
        );

        assertDoesNotThrow(() -> notificationService.sendNotification(request));

        verifyNoInteractions(mailSender, smsNotificationService);
    }

    @Test
    void sendNotification_shouldIgnoreUnknownType() {
        NotificationRequest request = request(
                "user@example.com",
                "Push Notification",
                "You have a new message.",
                "PUSH"
        );

        assertDoesNotThrow(() -> notificationService.sendNotification(request));

        verifyNoInteractions(mailSender, smsNotificationService);
    }

    @Test
    void sendNotification_shouldIgnoreNullType() {
        NotificationRequest request = request(
                "user@example.com",
                "Missing Type",
                "Test message",
                null
        );

        assertDoesNotThrow(() -> notificationService.sendNotification(request));

        verifyNoInteractions(mailSender, smsNotificationService);
    }
}
