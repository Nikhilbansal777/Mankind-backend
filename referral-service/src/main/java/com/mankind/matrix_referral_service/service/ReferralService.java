package com.mankind.matrix_referral_service.service;

import com.mankind.matrix_referral_service.dto.ReferralCodeResponse;
import com.mankind.matrix_referral_service.model.ReferralCode;
import com.mankind.matrix_referral_service.repository.ReferralCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.security.SecureRandom;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReferralService {

    private static final String CODE_PREFIX = "REF";
    private static final int CODE_RANDOM_LENGTH = 8;
    private static final String ALPHANUM = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    private final ReferralCodeRepository referralCodeRepository;
    private final CurrentUserService currentUserService;
    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public ReferralCodeResponse createOrGetCurrentUserReferralCode() {
        Long userId = currentUserService.getCurrentUserId();

        ReferralCode existingReferral = referralCodeRepository.findByUserId(userId)
                .orElse(null);

        if (existingReferral != null && Boolean.TRUE.equals(existingReferral.getIsActive())) {
            return toResponse(existingReferral);
        }

        String generatedCode = generateUniqueReferralCode();

        ReferralCode referral = existingReferral != null ? existingReferral : new ReferralCode();
        referral.setUserId(userId);
        referral.setReferralCode(generatedCode);
        referral.setIsActive(true);

        ReferralCode savedReferral = referralCodeRepository.save(referral);
        log.info("Referral code created for userId {}: {}", userId, generatedCode);

        return toResponse(savedReferral);
    }

    public ReferralCodeResponse getCurrentUserReferralCode() {
        Long userId = currentUserService.getCurrentUserId();
        ReferralCode referral = referralCodeRepository.findByUserId(userId)
                .filter(code -> Boolean.TRUE.equals(code.getIsActive()))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Referral code not found for user"));

        return toResponse(referral);
    }

    public boolean validateReferralCode(String referralCode) {
        if (referralCode == null || referralCode.isBlank()) {
            return false;
        }

        return referralCodeRepository.findByReferralCodeAndIsActiveTrue(referralCode.toUpperCase(Locale.ROOT)).isPresent();
    }

    private ReferralCodeResponse toResponse(ReferralCode referralCode) {
        return ReferralCodeResponse.builder()
                .userId(referralCode.getUserId())
                .referralCode(referralCode.getReferralCode())
                .createdAt(referralCode.getCreatedAt())
                .build();
    }

    private String generateUniqueReferralCode() {
        for (int attempt = 0; attempt < 20; attempt++) {
            String candidate = CODE_PREFIX + randomCodePart(CODE_RANDOM_LENGTH);
            if (!referralCodeRepository.existsByReferralCode(candidate)) {
                return candidate;
            }
        }

        throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                "Failed to generate a unique referral code");
    }

    private String randomCodePart(int length) {
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(ALPHANUM.length());
            builder.append(ALPHANUM.charAt(index));
        }
        return builder.toString();
    }
}
