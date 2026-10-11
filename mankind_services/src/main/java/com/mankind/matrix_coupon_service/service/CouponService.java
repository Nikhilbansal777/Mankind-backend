package com.mankind.matrix_coupon_service.service;

import com.mankind.matrix_coupon_service.dto.CreateCouponRequest;
import com.mankind.matrix_coupon_service.dto.UseCouponRequest;
import com.mankind.matrix_coupon_service.model.Coupon;
import com.mankind.matrix_coupon_service.model.CouponUsage;
import com.mankind.matrix_coupon_service.repository.CouponRepository;
import com.mankind.matrix_coupon_service.repository.CouponUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final CurrentUserService currentUserService;
    private final RoleVerificationService roleVerificationService;

    public Page<Coupon> getAllActiveCoupons(Pageable pageable) {
        log.debug("Fetching all active coupons with pagination (no date validation)");
        return couponRepository.findActiveCouponsOnly(pageable);
    }

    public Coupon getCouponById(Long id) {
        log.debug("Fetching coupon by ID: {}", id);
        return couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
    }

    public Coupon validateCoupon(String code) {
        log.debug("Validating coupon code: {} for user: {}", code, currentUserService.getCurrentUserId());
        
        // Find the coupon by code
        Coupon coupon = couponRepository.findByCodeAndIsActiveTrue(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found or inactive: " + code));
        
        // Check if coupon is active
        if (!coupon.getIsActive()) {
            throw new RuntimeException("Coupon is not active: " + code);
        }
        
        // Check if coupon is within valid date range
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidTo())) {
            throw new RuntimeException("Coupon is not valid at this time: " + code);
        }
        
        // Check if coupon has reached maximum usage
        if (coupon.getCurrentUsage() >= coupon.getMaxUsage()) {
            throw new RuntimeException("Coupon has reached maximum usage limit: " + code);
        }
        
        // Check if user has already used this coupon (if one-time use per user)
        if (coupon.getOneTimeUsePerUser()) {
            Long usageCount = couponRepository.countUsageByCouponAndUser(coupon.getId(), currentUserService.getCurrentUserId());
            if (usageCount > 0) {
                throw new RuntimeException("Coupon already used by this user: " + code);
            }
        }
        
        log.info("Coupon validated successfully: {} for user: {}", code, currentUserService.getCurrentUserId());
        return coupon;
    }

    @Transactional
    public Coupon useCoupon(UseCouponRequest request) {
        log.debug("Using coupon code: {} for user: {}", request.getCode(), currentUserService.getCurrentUserId());
        
        // First validate the coupon (includes all checks: active, dates, max usage, one-time use)
        Coupon coupon = validateCoupon(request.getCode());
        
        // Increment current usage
        coupon.setCurrentUsage(coupon.getCurrentUsage() + 1);
        Coupon updatedCoupon = couponRepository.save(coupon);
        
        // Record usage in coupon_usage table with optional orderId
        couponUsageRepository.save(CouponUsage.builder()
                .coupon(updatedCoupon)
                .userId(currentUserService.getCurrentUserId())
                .orderId(request.getOrderId())
                .build());
        
        log.info("Coupon used successfully: {} for user: {} with orderId: {}", 
                request.getCode(), currentUserService.getCurrentUserId(), request.getOrderId());
        return updatedCoupon;
    }

    public Coupon createCoupon(Coupon coupon) {
        log.debug("Creating new coupon: {}", coupon.getCode());
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists: " + coupon.getCode());
        }
        coupon.setCurrentUsage(0);
        coupon.setIsActive(true);
        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon created successfully: {}", savedCoupon.getCode());
        return savedCoupon;
    }

    public Coupon createCoupon(CreateCouponRequest request) {
        // Verify admin role for coupon creation
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        log.debug("Creating new coupon: {}", request.getCode());
        
        // Check if coupon code already exists among active coupons
        if (couponRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new RuntimeException("Coupon code already exists: " + request.getCode());
        }
        
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .name(request.getName())
                .description(request.getDescription())
                .type(request.getType())
                .value(request.getValue())
                .minimumOrderAmount(request.getMinimumOrderAmount())
                .maxUsage(request.getMaxUsage())
                .currentUsage(0) // Always start with 0 usage
                .validFrom(request.getValidFrom())
                .validTo(request.getValidTo())
                .oneTimeUsePerUser(request.getOneTimeUsePerUser())
                .isActive(true)
                .build();
        
        Coupon savedCoupon = couponRepository.save(coupon);
        log.info("Coupon created successfully: {}", savedCoupon.getCode());
        return savedCoupon;
    }

    public Coupon updateCoupon(Long id, CreateCouponRequest request) {
        // Verify admin role for coupon update
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        log.debug("Updating coupon with id: {}", id);
        
        Coupon existingCoupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        
        // Check if the new code already exists among active coupons (excluding current coupon)
        if (!existingCoupon.getCode().equals(request.getCode()) && 
            couponRepository.existsByCodeAndIsActiveTrue(request.getCode())) {
            throw new RuntimeException("Coupon code already exists: " + request.getCode());
        }
        
        // Preserve the existing currentUsage value
        Integer currentUsage = existingCoupon.getCurrentUsage();
        
        existingCoupon.setCode(request.getCode());
        existingCoupon.setName(request.getName());
        existingCoupon.setDescription(request.getDescription());
        existingCoupon.setType(request.getType());
        existingCoupon.setValue(request.getValue());
        existingCoupon.setMinimumOrderAmount(request.getMinimumOrderAmount());
        existingCoupon.setMaxUsage(request.getMaxUsage());
        existingCoupon.setCurrentUsage(currentUsage); // Keep existing usage count
        existingCoupon.setValidFrom(request.getValidFrom());
        existingCoupon.setValidTo(request.getValidTo());
        existingCoupon.setOneTimeUsePerUser(request.getOneTimeUsePerUser());
        
        Coupon updatedCoupon = couponRepository.save(existingCoupon);
        log.info("Coupon updated successfully: {}", updatedCoupon.getCode());
        return updatedCoupon;
    }

    public void deleteCoupon(Long id) {
        // Verify admin role for coupon deletion
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        log.debug("Deactivating coupon with id: {}", id);
        
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found with id: " + id));
        
        // Soft delete - set isActive to false instead of deleting the record
        coupon.setIsActive(false);
        couponRepository.save(coupon);
        
        log.info("Coupon deactivated successfully: {}", coupon.getCode());
    }
} 