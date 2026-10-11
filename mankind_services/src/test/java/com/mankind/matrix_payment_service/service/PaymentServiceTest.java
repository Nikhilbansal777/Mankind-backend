package com.mankind.matrix_payment_service.service;

import com.mankind.matrix_payment_service.dto.CreatePaymentIntentRequest;
import com.mankind.matrix_payment_service.dto.PaymentIntentResponse;
import com.mankind.matrix_payment_service.dto.PaymentVerificationRequest;
import com.mankind.matrix_payment_service.dto.PaymentVerificationResponse;
import com.mankind.matrix_payment_service.exception.ProviderNotImplementedException;
import com.mankind.matrix_payment_service.model.Payment;
import com.mankind.matrix_payment_service.model.PaymentProvider;
import com.mankind.matrix_payment_service.model.PaymentStatus;
import com.mankind.matrix_payment_service.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private StripePaymentService stripePaymentService;

    @InjectMocks
    private PaymentService paymentService;

    private CreatePaymentIntentRequest createRequest;
    private Payment payment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @BeforeEach
    void setUp() {
        createdAt = LocalDateTime.of(2026, 7, 21, 10, 0);
        updatedAt = LocalDateTime.of(2026, 7, 21, 11, 0);

        createRequest = CreatePaymentIntentRequest.builder()
                .userId("user-101")
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .description("Test payment")
                .provider(PaymentProvider.STRIPE)
                .build();

        payment = Payment.builder()
                .id(1L)
                .stripePaymentIntentId("pi_test_123")
                .userId("user-101")
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .provider(PaymentProvider.STRIPE)
                .status(PaymentStatus.SUCCEEDED)
                .description("Test payment")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();
    }

    @Test
    void createPaymentIntent_withStripe_shouldReturnResponse() {
        PaymentIntentResponse expectedResponse = PaymentIntentResponse.builder()
                .id(1L)
                .stripePaymentIntentId("pi_test_123")
                .userId("user-101")
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .provider(PaymentProvider.STRIPE)
                .status(PaymentStatus.PENDING)
                .description("Test payment")
                .clientSecret("secret_123")
                .createdAt(createdAt)
                .updatedAt(updatedAt)
                .build();

        when(stripePaymentService.createPaymentIntent(createRequest))
                .thenReturn(expectedResponse);

        PaymentIntentResponse actualResponse =
                paymentService.createPaymentIntent(createRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse, actualResponse);
        assertEquals("pi_test_123", actualResponse.getStripePaymentIntentId());
        assertEquals("secret_123", actualResponse.getClientSecret());

        verify(stripePaymentService).createPaymentIntent(createRequest);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void createPaymentIntent_withUnsupportedProvider_shouldThrowException() {
        PaymentProvider unsupportedProvider = getUnsupportedProvider();

        CreatePaymentIntentRequest request = CreatePaymentIntentRequest.builder()
                .userId("user-101")
                .amount(new BigDecimal("150.00"))
                .currency("USD")
                .provider(unsupportedProvider)
                .build();

        ProviderNotImplementedException exception = assertThrows(
                ProviderNotImplementedException.class,
                () -> paymentService.createPaymentIntent(request)
        );

        assertTrue(exception.getMessage().contains(unsupportedProvider.toString()));

        verifyNoInteractions(stripePaymentService);
        verifyNoInteractions(paymentRepository);
    }

    @Test
    void getPaymentIntent_whenPaymentExists_shouldReturnMappedResponse() {
        when(paymentRepository.findByStripePaymentIntentId("pi_test_123"))
                .thenReturn(Optional.of(payment));

        PaymentIntentResponse response =
                paymentService.getPaymentIntent("pi_test_123");

        assertNotNull(response);

        assertAll(
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals("pi_test_123",
                        response.getStripePaymentIntentId()),
                () -> assertEquals("user-101", response.getUserId()),
                () -> assertEquals(new BigDecimal("150.00"),
                        response.getAmount()),
                () -> assertEquals("USD", response.getCurrency()),
                () -> assertEquals(PaymentProvider.STRIPE,
                        response.getProvider()),
                () -> assertEquals(PaymentStatus.SUCCEEDED,
                        response.getStatus()),
                () -> assertEquals("Test payment",
                        response.getDescription()),
                () -> assertEquals(createdAt, response.getCreatedAt()),
                () -> assertEquals(updatedAt, response.getUpdatedAt())
        );

        verify(paymentRepository)
                .findByStripePaymentIntentId("pi_test_123");
        verifyNoInteractions(stripePaymentService);
    }

    @Test
    void getPaymentIntent_whenPaymentDoesNotExist_shouldThrowException() {
        when(paymentRepository.findByStripePaymentIntentId("pi_missing"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.getPaymentIntent("pi_missing")
        );

        assertEquals(
                "Payment not found: pi_missing",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findByStripePaymentIntentId("pi_missing");
        verifyNoInteractions(stripePaymentService);
    }

    @Test
    void verifyPayment_withStripe_shouldReturnResponseWithOrderId() {
        PaymentVerificationRequest request =
                PaymentVerificationRequest.builder()
                        .orderId("order-1001")
                        .paymentIntentId("pi_test_123")
                        .build();

        PaymentVerificationResponse stripeResponse =
                PaymentVerificationResponse.builder()
                        .id(1L)
                        .stripePaymentIntentId("pi_test_123")
                        .userId("user-101")
                        .amount(new BigDecimal("150.00"))
                        .currency("USD")
                        .provider(PaymentProvider.STRIPE)
                        .status(PaymentStatus.SUCCEEDED)
                        .description("Test payment")
                        .paymentSucceeded(true)
                        .createdAt(createdAt)
                        .updatedAt(updatedAt)
                        .build();

        when(paymentRepository.findByStripePaymentIntentId("pi_test_123"))
                .thenReturn(Optional.of(payment));

        when(stripePaymentService.verifyPaymentWithStripe("pi_test_123"))
                .thenReturn(stripeResponse);

        PaymentVerificationResponse response =
                paymentService.verifyPayment(request);

        assertNotNull(response);

        assertAll(
                () -> assertEquals(1L, response.getId()),
                () -> assertEquals("pi_test_123",
                        response.getStripePaymentIntentId()),
                () -> assertEquals("user-101", response.getUserId()),
                () -> assertEquals("order-1001", response.getOrderId()),
                () -> assertEquals(new BigDecimal("150.00"),
                        response.getAmount()),
                () -> assertEquals("USD", response.getCurrency()),
                () -> assertEquals(PaymentProvider.STRIPE,
                        response.getProvider()),
                () -> assertEquals(PaymentStatus.SUCCEEDED,
                        response.getStatus()),
                () -> assertEquals("Test payment",
                        response.getDescription()),
                () -> assertTrue(response.isPaymentSucceeded()),
                () -> assertEquals(createdAt, response.getCreatedAt()),
                () -> assertEquals(updatedAt, response.getUpdatedAt())
        );

        verify(paymentRepository)
                .findByStripePaymentIntentId("pi_test_123");

        verify(stripePaymentService)
                .verifyPaymentWithStripe("pi_test_123");
    }

    @Test
    void verifyPayment_whenPaymentDoesNotExist_shouldThrowException() {
        PaymentVerificationRequest request =
                PaymentVerificationRequest.builder()
                        .orderId("order-1001")
                        .paymentIntentId("pi_missing")
                        .build();

        when(paymentRepository.findByStripePaymentIntentId("pi_missing"))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> paymentService.verifyPayment(request)
        );

        assertEquals(
                "Payment not found: pi_missing",
                exception.getMessage()
        );

        verify(paymentRepository)
                .findByStripePaymentIntentId("pi_missing");

        verifyNoInteractions(stripePaymentService);
    }

    @Test
    void verifyPayment_withUnsupportedProvider_shouldThrowException() {
        PaymentProvider unsupportedProvider = getUnsupportedProvider();

        Payment unsupportedPayment = Payment.builder()
                .id(2L)
                .stripePaymentIntentId("provider_payment_123")
                .userId("user-102")
                .amount(new BigDecimal("75.00"))
                .currency("USD")
                .provider(unsupportedProvider)
                .status(PaymentStatus.PENDING)
                .build();

        PaymentVerificationRequest request =
                PaymentVerificationRequest.builder()
                        .orderId("order-1002")
                        .paymentIntentId("provider_payment_123")
                        .build();

        when(paymentRepository.findByStripePaymentIntentId(
                "provider_payment_123"
        )).thenReturn(Optional.of(unsupportedPayment));

        ProviderNotImplementedException exception = assertThrows(
                ProviderNotImplementedException.class,
                () -> paymentService.verifyPayment(request)
        );

        assertTrue(exception.getMessage().contains(
                unsupportedProvider.toString()
        ));

        verify(paymentRepository)
                .findByStripePaymentIntentId("provider_payment_123");

        verifyNoInteractions(stripePaymentService);
    }

    private PaymentProvider getUnsupportedProvider() {
        return Arrays.stream(PaymentProvider.values())
                .filter(provider -> provider != PaymentProvider.STRIPE)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "PaymentProvider must contain at least one " +
                        "provider other than STRIPE to test the default branch"
                ));
    }
}