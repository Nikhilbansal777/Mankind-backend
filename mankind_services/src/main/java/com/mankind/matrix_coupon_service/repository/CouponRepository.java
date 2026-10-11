package com.mankind.matrix_coupon_service.repository;

import com.mankind.matrix_coupon_service.model.Coupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeAndIsActiveTrue(String code);

    boolean existsByCodeAndIsActiveTrue(String code);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true")
    Page<Coupon> findActiveCouponsOnly(Pageable pageable);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :now AND c.validTo >= :now " +
           "AND c.currentUsage < c.maxUsage")
    List<Coupon> findActivePublicCoupons(@Param("now") LocalDateTime now);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :now AND c.validTo >= :now " +
           "AND c.currentUsage < c.maxUsage")
    Page<Coupon> findActiveCoupons(@Param("now") LocalDateTime now, Pageable pageable);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :now AND c.validTo >= :now " +
           "AND c.currentUsage < c.maxUsage " +
           "AND c.code = :code")
    Optional<Coupon> findActiveCouponByCode(@Param("code") String code, @Param("now") LocalDateTime now);

    boolean existsByCode(String code);

    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.userId = :userId")
    Long countUsageByCouponAndUser(@Param("couponId") Long couponId, @Param("userId") Long userId);

    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId")
    Long countTotalUsageByCoupon(@Param("couponId") Long couponId);

    @Query("SELECT c FROM Coupon c WHERE c.isActive = true " +
           "AND c.validFrom <= :now AND c.validTo >= :now " +
           "AND c.currentUsage < c.maxUsage")
    List<Coupon> findValidCoupons(@Param("now") LocalDateTime now);
} 