package com.mankind.matrix_referral_service.service;

import com.mankind.matrix_referral_service.dto.ReferralCodeResponse;
import com.mankind.matrix_referral_service.model.ReferralCode;
import com.mankind.matrix_referral_service.repository.ReferralCodeRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ReferralServiceTest {

    @Test
    void createOrGetCurrentUserReferralCode_returnsExistingActiveCode() {
        RepoStub repoStub = new RepoStub();
        ReferralCode existing = ReferralCode.builder()
                .id(10L)
                .userId(42L)
                .referralCode("REFEXIST12")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();
        repoStub.seed(existing);

        ReferralService service = new ReferralService(repoStub.proxy(), new CurrentUserServiceStub(42L));

        ReferralCodeResponse response = service.createOrGetCurrentUserReferralCode();

        assertEquals(42L, response.getUserId());
        assertEquals("REFEXIST12", response.getReferralCode());
        assertEquals(0, repoStub.saveCalls);
    }

    @Test
    void createOrGetCurrentUserReferralCode_createsNewCodeWhenMissing() {
        RepoStub repoStub = new RepoStub();
        ReferralService service = new ReferralService(repoStub.proxy(), new CurrentUserServiceStub(99L));

        ReferralCodeResponse response = service.createOrGetCurrentUserReferralCode();

        assertEquals(99L, response.getUserId());
        assertNotNull(response.getReferralCode());
        assertTrue(response.getReferralCode().startsWith("REF"));
        assertEquals(11, response.getReferralCode().length());
        assertEquals(1, repoStub.saveCalls);
    }

    @Test
    void getCurrentUserReferralCode_throwsNotFoundWhenNoActiveCode() {
        RepoStub repoStub = new RepoStub();
        ReferralService service = new ReferralService(repoStub.proxy(), new CurrentUserServiceStub(77L));

        ResponseStatusException ex = assertThrows(ResponseStatusException.class, service::getCurrentUserReferralCode);

        assertEquals(HttpStatus.NOT_FOUND, ex.getStatusCode());
    }

    @Test
    void validateReferralCode_returnsFalseForBlankAndTrueForExistingCaseInsensitive() {
        RepoStub repoStub = new RepoStub();
        repoStub.seed(ReferralCode.builder()
                .id(1L)
                .userId(5L)
                .referralCode("REFABC1234")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build());

        ReferralService service = new ReferralService(repoStub.proxy(), new CurrentUserServiceStub(5L));

        assertFalse(service.validateReferralCode("  "));
        assertTrue(service.validateReferralCode("refabc1234".toLowerCase(Locale.ROOT)));
        assertFalse(service.validateReferralCode("REFDOESNOT"));
    }

    private static final class CurrentUserServiceStub extends CurrentUserService {
        private final Long userId;

        private CurrentUserServiceStub(Long userId) {
            super(() -> null);
            this.userId = userId;
        }

        @Override
        public Long getCurrentUserId() {
            return userId;
        }
    }

    private static final class RepoStub implements InvocationHandler {
        private final Map<Long, ReferralCode> byUserId = new HashMap<>();
        private final Map<String, ReferralCode> byCode = new HashMap<>();
        private long sequence = 100L;
        private int saveCalls = 0;

        private ReferralCodeRepository proxy() {
            return (ReferralCodeRepository) Proxy.newProxyInstance(
                    ReferralCodeRepository.class.getClassLoader(),
                    new Class<?>[]{ReferralCodeRepository.class},
                    this
            );
        }

        private void seed(ReferralCode referralCode) {
            byUserId.put(referralCode.getUserId(), referralCode);
            byCode.put(referralCode.getReferralCode(), referralCode);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) {
            String name = method.getName();

            return switch (name) {
                case "findByUserId" -> Optional.ofNullable(byUserId.get((Long) args[0]));
                case "findByReferralCodeAndIsActiveTrue" -> {
                    String code = (String) args[0];
                    ReferralCode referralCode = byCode.get(code);
                    yield referralCode != null && Boolean.TRUE.equals(referralCode.getIsActive())
                            ? Optional.of(referralCode)
                            : Optional.empty();
                }
                case "existsByReferralCode" -> byCode.containsKey((String) args[0]);
                case "save" -> {
                    ReferralCode entity = (ReferralCode) args[0];
                    if (entity.getId() == null) {
                        entity.setId(++sequence);
                    }
                    if (entity.getCreatedAt() == null) {
                        entity.setCreatedAt(LocalDateTime.now());
                    }
                    byUserId.put(entity.getUserId(), entity);
                    byCode.put(entity.getReferralCode(), entity);
                    saveCalls++;
                    yield entity;
                }
                case "toString" -> "RepoStubProxy";
                case "hashCode" -> System.identityHashCode(this);
                case "equals" -> proxy == args[0];
                default -> throw new UnsupportedOperationException("Unsupported repository method in test: " + name);
            };
        }
    }
}
