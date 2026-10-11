package com.mankind.matrix_referral_service.repository;

import com.mankind.matrix_referral_service.model.ReferralCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReferralCodeRepository extends JpaRepository<ReferralCode, Long> {

    Optional<ReferralCode> findByUserId(Long userId);

    Optional<ReferralCode> findByReferralCodeAndIsActiveTrue(String referralCode);

    boolean existsByReferralCode(String referralCode);
}
