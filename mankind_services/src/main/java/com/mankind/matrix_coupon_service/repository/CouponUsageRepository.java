package com.mankind.matrix_coupon_service.repository;

import com.mankind.matrix_coupon_service.model.CouponUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long> {

    @Query("SELECT cu FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.userId = :userId")
    List<CouponUsage> findByCouponIdAndUserId(@Param("couponId") Long couponId, @Param("userId") Long userId);

    @Query("SELECT cu FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.userId = :userId AND cu.orderId = :orderId")
    List<CouponUsage> findByCouponIdAndUserIdAndOrderId(@Param("couponId") Long couponId, @Param("userId") Long userId, @Param("orderId") Long orderId);

    @Query("SELECT COUNT(cu) FROM CouponUsage cu WHERE cu.coupon.id = :couponId AND cu.userId = :userId")
    Long countByCouponIdAndUserId(@Param("couponId") Long couponId, @Param("userId") Long userId);
} 