package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.recentlyviewed.RecentlyViewedProductResponseDTO;
import com.mankind.matrix_product_service.model.RecentlyViewedProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RecentlyViewedProductMapper {

    private final ProductMapper productMapper;

    /**
     * Convert a RecentlyViewedProduct entity to a RecentlyViewedProductResponseDTO
     */
    public RecentlyViewedProductResponseDTO toDto(RecentlyViewedProduct entity) {
        if (entity == null) {
            return null;
        }

        return RecentlyViewedProductResponseDTO.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .product(productMapper.toResponseDTO(entity.getProduct()))
                .viewedAt(entity.getViewedAt())
                .lastViewedAt(entity.getLastViewedAt())
                .build();
    }
}
