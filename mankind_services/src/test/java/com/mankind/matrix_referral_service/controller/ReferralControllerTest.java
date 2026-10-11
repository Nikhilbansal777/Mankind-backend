package com.mankind.matrix_referral_service.controller;

import com.mankind.matrix_referral_service.dto.ReferralCodeResponse;
import com.mankind.matrix_referral_service.dto.ReferralValidationResponse;
import com.mankind.matrix_referral_service.service.CurrentUserService;
import com.mankind.matrix_referral_service.service.ReferralService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReferralControllerTest {

    @Test
    void createReferralCodeForCurrentUser_returnsCreated() {
        StubReferralService service = new StubReferralService();
        ReferralController controller = new ReferralController(service);

        ResponseEntity<ReferralCodeResponse> response = controller.createReferralCodeForCurrentUser();

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("REFTEST001", response.getBody().getReferralCode());
        assertTrue(service.createCalled);
    }

    @Test
    void getCurrentUserReferralCode_returnsOk() {
        StubReferralService service = new StubReferralService();
        ReferralController controller = new ReferralController(service);

        ResponseEntity<ReferralCodeResponse> response = controller.getCurrentUserReferralCode();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(7L, response.getBody().getUserId());
        assertTrue(service.getCalled);
    }

    @Test
    void validateReferralCode_returnsValidationResponse() {
        StubReferralService service = new StubReferralService();
        ReferralController controller = new ReferralController(service);

        ResponseEntity<ReferralValidationResponse> response = controller.validateReferralCode("REFANY001");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().isValid());
        assertEquals("REFANY001", service.lastValidatedCode);
    }

    private static final class StubReferralService extends ReferralService {
        private boolean createCalled = false;
        private boolean getCalled = false;
        private String lastValidatedCode;

        private StubReferralService() {
            super(null, new CurrentUserService(() -> null));
        }

        @Override
        public ReferralCodeResponse createOrGetCurrentUserReferralCode() {
            createCalled = true;
            return ReferralCodeResponse.builder()
                    .userId(7L)
                    .referralCode("REFTEST001")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Override
        public ReferralCodeResponse getCurrentUserReferralCode() {
            getCalled = true;
            return ReferralCodeResponse.builder()
                    .userId(7L)
                    .referralCode("REFTEST001")
                    .createdAt(LocalDateTime.now())
                    .build();
        }

        @Override
        public boolean validateReferralCode(String referralCode) {
            lastValidatedCode = referralCode;
            return true;
        }
    }
}
