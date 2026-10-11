package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.supplier.SupplierDTO;
import com.mankind.api.product.dto.supplier.SupplierResponseDTO;
import com.mankind.matrix_product_service.model.Supplier;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SupplierMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", source = "isActive", defaultValue = "true")
    Supplier toEntity(SupplierDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "products", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", source = "isActive")
    void updateEntity(@MappingTarget Supplier supplier, SupplierDTO dto);

    @Mapping(target = "productIds", expression = "java(mapProductIds(supplier))")
    @Mapping(target = "active", source = "active")
    SupplierResponseDTO toResponseDTO(Supplier supplier);

    default List<Long> mapProductIds(Supplier supplier) {
        if (supplier.getProducts() == null) {
            return List.of();
        }
        return supplier.getProducts().stream()
                .map(product -> product.getId())
                .collect(Collectors.toList());
    }
}
