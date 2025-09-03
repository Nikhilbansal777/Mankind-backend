package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.corporate.CorporatePriceDTO;
import com.mankind.api.product.dto.corporate.CorporatePriceResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.CorporatePriceMapper;
import com.mankind.matrix_product_service.model.CorporatePrice;
import com.mankind.matrix_product_service.model.Product;
import com.mankind.matrix_product_service.repository.CorporatePriceRepository;
import com.mankind.matrix_product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CorporatePriceService {
    private final CorporatePriceRepository corporatePriceRepository;
    private final ProductRepository productRepository;
    private final CorporatePriceMapper corporatePriceMapper;

    /**
     * Update or create a corporate price for a specific product
     * 
     * @param corporateId the corporate ID
     * @param productId the product ID
     * @param priceDTO the price data
     * @param username the username of the authenticated user
     * @return the updated or created corporate price
     */
    @Transactional
    public CorporatePriceResponseDTO updatePrice(Long corporateId, Long productId, CorporatePriceDTO priceDTO, String username) {
        log.info("Updating price for corporate ID: {}, product ID: {}", corporateId, productId);
        
        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with ID: " + productId));
        
        // Check if there's an existing current price
        LocalDateTime now = LocalDateTime.now();
        CorporatePrice existingPrice = corporatePriceRepository.findCurrentPrice(corporateId, productId, now)
                .orElse(null);
        
        CorporatePrice corporatePrice;
        if (existingPrice != null) {
            // If there's an existing price, update it or expire it and create a new one
            if (existingPrice.getEffectiveTo() == null || existingPrice.getEffectiveTo().isAfter(now)) {
                // If the existing price is still effective, expire it
                existingPrice.setEffectiveTo(now);
                corporatePriceRepository.save(existingPrice);
            }
            
            // Create a new price record
            corporatePrice = corporatePriceMapper.createWithCorporateId(priceDTO, corporateId, username);
            corporatePrice.setProductId(productId);
        } else {
            // If there's no existing price, create a new one
            corporatePrice = corporatePriceMapper.createWithCorporateId(priceDTO, corporateId, username);
            corporatePrice.setProductId(productId);
        }
        
        // Save the new price
        CorporatePrice savedPrice = corporatePriceRepository.save(corporatePrice);
        log.info("Price updated successfully for corporate ID: {}, product ID: {}", corporateId, productId);
        
        return corporatePriceMapper.toResponseDTO(savedPrice);
    }
    
    /**
     * Get the current price for a specific corporate and product
     * 
     * @param corporateId the corporate ID
     * @param productId the product ID
     * @return the current price
     */
    @Transactional(readOnly = true)
    public CorporatePriceResponseDTO getPriceByCorpIdAndProductId(Long corporateId, Long productId) {
        log.info("Getting price for corporate ID: {}, product ID: {}", corporateId, productId);
        
        LocalDateTime now = LocalDateTime.now();
        CorporatePrice price = corporatePriceRepository.findCurrentPrice(corporateId, productId, now)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No current price found for corporate ID: " + corporateId + " and product ID: " + productId));
        
        return corporatePriceMapper.toResponseDTO(price);
    }
    
    /**
     * Get all current prices for a specific corporate
     * 
     * @param corporateId the corporate ID
     * @return list of all current prices
     */
    @Transactional(readOnly = true)
    public List<CorporatePriceResponseDTO> getAllPricesByCorpId(Long corporateId) {
        log.info("Getting all prices for corporate ID: {}", corporateId);
        
        LocalDateTime now = LocalDateTime.now();
        List<CorporatePrice> prices = corporatePriceRepository.findAllCurrentPricesByCorporateId(corporateId, now);
        
        return corporatePriceMapper.toResponseDTOList(prices);
    }
    
    /**
     * Get all prices (including historical) for a specific corporate and product
     * 
     * @param corporateId the corporate ID
     * @param productId the product ID
     * @return list of all prices
     */
    @Transactional(readOnly = true)
    public List<CorporatePriceResponseDTO> getAllPriceHistoryByCorpIdAndProductId(Long corporateId, Long productId) {
        log.info("Getting price history for corporate ID: {}, product ID: {}", corporateId, productId);
        
        List<CorporatePrice> prices = corporatePriceRepository
                .findByCorporateIdAndProductIdOrderByEffectiveFromDesc(corporateId, productId);
        
        return corporatePriceMapper.toResponseDTOList(prices);
    }
}