package com.mankind.matrix_product_service.repository;

import com.mankind.matrix_product_service.model.CorporatePrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CorporatePriceRepository extends JpaRepository<CorporatePrice, Long> {
    
    /**
     * Find the current valid price for a specific corporate and product
     * @param corporateId the corporate ID
     * @param productId the product ID
     * @param currentDate the current date to check validity
     * @return the valid corporate price
     */
    @Query("SELECT cp FROM CorporatePrice cp WHERE cp.corporateId = :corporateId " +
           "AND cp.productId = :productId " +
           "AND cp.effectiveFrom <= :currentDate " +
           "AND (cp.effectiveTo IS NULL OR cp.effectiveTo > :currentDate) " +
           "ORDER BY cp.effectiveFrom DESC")
    Optional<CorporatePrice> findCurrentPrice(
            @Param("corporateId") Long corporateId,
            @Param("productId") Long productId,
            @Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find all current valid prices for a specific corporate
     * @param corporateId the corporate ID
     * @param currentDate the current date to check validity
     * @return list of valid corporate prices
     */
    @Query("SELECT cp FROM CorporatePrice cp WHERE cp.corporateId = :corporateId " +
           "AND cp.effectiveFrom <= :currentDate " +
           "AND (cp.effectiveTo IS NULL OR cp.effectiveTo > :currentDate) " +
           "ORDER BY cp.productId, cp.effectiveFrom DESC")
    List<CorporatePrice> findAllCurrentPricesByCorporateId(
            @Param("corporateId") Long corporateId,
            @Param("currentDate") LocalDateTime currentDate);
    
    /**
     * Find all prices (including historical) for a specific corporate and product
     * @param corporateId the corporate ID
     * @param productId the product ID
     * @return list of all corporate prices for the product
     */
    List<CorporatePrice> findByCorporateIdAndProductIdOrderByEffectiveFromDesc(
            Long corporateId, Long productId);
    
    /**
     * Find all prices (including historical) for a specific corporate
     * @param corporateId the corporate ID
     * @return list of all corporate prices
     */
    List<CorporatePrice> findByCorporateIdOrderByProductIdAscEffectiveFromDesc(Long corporateId);
}