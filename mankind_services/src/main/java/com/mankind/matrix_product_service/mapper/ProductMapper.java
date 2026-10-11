package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.inventory.InventoryStatusDTO;
import com.mankind.api.product.dto.product.ProductDTO;
import com.mankind.api.product.dto.product.ProductResponseDTO;
import com.mankind.matrix_product_service.model.Inventory;
import com.mankind.matrix_product_service.model.Product;
import org.mapstruct.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {CategoryMapper.class})
public interface ProductMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", constant = "true")
    Product toEntity(ProductDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "name", ignore = true)
    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "description", ignore = true)
    @Mapping(target = "sku", ignore = true)
    @Mapping(target = "brand", ignore = true)
    @Mapping(target = "model", ignore = true)
    @Mapping(target = "specifications", ignore = true)
    @Mapping(target = "images", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductDTO dto);

    @Mapping(target = "inventoryStatus", expression = "java(mapInventoryStatus(product.getInventory()))")
    @Mapping(target = "images", source = "images", qualifiedByName = "filterImages")
    @Mapping(target = "specifications", source = "specifications", qualifiedByName = "filterSpecifications")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "averageRating", source = "averageRating")
    ProductResponseDTO toResponseDTO(Product product);

    @Named("filterImages")
    default List<String> filterImages(List<String> images) {
        if (images == null) {
            return new ArrayList<>();
        }
        return images.stream()
            .filter(image -> image != null && !image.trim().isEmpty())
            .collect(Collectors.toList());
    }

    @Named("filterSpecifications")
    default Map<String, String> filterSpecifications(Map<String, String> specifications) {
        if (specifications == null) {
            return new HashMap<>();
        }
        return specifications.entrySet().stream()
            .filter(entry -> entry.getKey() != null && !entry.getKey().trim().isEmpty() &&
                           entry.getValue() != null && !entry.getValue().trim().isEmpty())
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (v1, v2) -> v1,  // In case of duplicate keys, keep the first one
                HashMap::new
            ));
    }

    default InventoryStatusDTO mapInventoryStatus(Inventory inventory) {
        if (inventory == null) {
            return InventoryStatusDTO.builder()
                .availableQuantity(BigDecimal.ZERO)
                .reservedQuantity(BigDecimal.ZERO)
                .soldQuantity(BigDecimal.ZERO)
                .totalQuantity(BigDecimal.ZERO)
                .status("NO_INVENTORY")
                .maxQuantityPerPurchase(null) // Setting maxQuantityPerPurchase to null to indicate no limit or not applicable
                .build();
        }

        BigDecimal totalQuantity = inventory.getAvailableQuantity().add(inventory.getReservedQuantity());
        return InventoryStatusDTO.builder()
            .productId(inventory.getProduct().getId())
            .productName(inventory.getProduct().getName())
            .price(inventory.getPrice())
            .priceDisplay(formatPriceDisplay(inventory.getPrice(), inventory.getCurrency()))
            .currency(inventory.getCurrency())
            .availableQuantity(inventory.getAvailableQuantity())
            .reservedQuantity(inventory.getReservedQuantity())
            .soldQuantity(inventory.getSoldQuantity())
            .totalQuantity(totalQuantity)
            .status(determineStatus(inventory))
            .maxQuantityPerPurchase(inventory.getMaxQuantityPerPurchase())
            .build();
    }

    default String formatPriceDisplay(BigDecimal price, String currency) {
        if (price == null || currency == null) {
            return null;
        }
        return String.format("%s %.2f", currency, price.doubleValue());
    }

    default String determineStatus(Inventory inventory) {
        if (inventory == null) {
            return "NO_INVENTORY";
        }
        
        BigDecimal total = inventory.getAvailableQuantity().add(inventory.getReservedQuantity());
        if (total.compareTo(BigDecimal.ZERO) <= 0) {
            return "OUT_OF_STOCK";
        }
        
        return "IN_STOCK";
    }
}
