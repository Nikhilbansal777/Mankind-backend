package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.corporate.CorporatePriceDTO;
import com.mankind.api.product.dto.corporate.CorporatePriceResponseDTO;
import com.mankind.matrix_product_service.model.CorporatePrice;
import org.mapstruct.*;

import java.time.LocalDateTime;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {ProductMapper.class})
public interface CorporatePriceMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "corporateId", ignore = true) // This will be set from the path variable
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "updatedBy", ignore = true) // This will be set from the authenticated user
    @Mapping(target = "updatedAt", ignore = true) // This will be set automatically
    @Mapping(target = "createdAt", ignore = true) // This will be set automatically
    CorporatePrice toEntity(CorporatePriceDTO dto);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "corporateId", ignore = true) // This will be preserved
    @Mapping(target = "productId", ignore = true) // This will be preserved
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "updatedBy", ignore = true) // This will be set from the authenticated user
    @Mapping(target = "updatedAt", ignore = true) // This will be set automatically
    @Mapping(target = "createdAt", ignore = true) // This will be preserved
    void updateEntityFromDTO(CorporatePriceDTO dto, @MappingTarget CorporatePrice entity);
    
    @Mapping(target = "product", source = "product", qualifiedByName = "toProductResponseDTO")
    CorporatePriceResponseDTO toResponseDTO(CorporatePrice entity);
    
    List<CorporatePriceResponseDTO> toResponseDTOList(List<CorporatePrice> entities);
    
    @Named("toProductResponseDTO")
    default com.mankind.api.product.dto.product.ProductResponseDTO toProductResponseDTO(com.mankind.matrix_product_service.model.Product product) {
        if (product == null) {
            return null;
        }
        return ((ProductMapper) this).toResponseDTO(product);
    }
    
    default CorporatePrice createWithCorporateId(CorporatePriceDTO dto, Long corporateId, String updatedBy) {
        if (dto == null) {
            return null;
        }
        
        CorporatePrice entity = toEntity(dto);
        entity.setCorporateId(corporateId);
        entity.setUpdatedBy(updatedBy);
        
        // Set default values if not provided
        if (entity.getEffectiveFrom() == null) {
            entity.setEffectiveFrom(LocalDateTime.now());
        }
        
        return entity;
    }
}