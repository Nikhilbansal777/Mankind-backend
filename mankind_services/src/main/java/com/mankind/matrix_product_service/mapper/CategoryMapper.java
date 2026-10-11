package com.mankind.matrix_product_service.mapper;


import com.mankind.api.product.dto.category.CategoryDTO;
import com.mankind.api.product.dto.category.CategoryResponseDTO;
import com.mankind.matrix_product_service.model.Category;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategoryMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Category toEntity(CategoryDTO dto);

    @Mapping(target = "parentId", source = "parent.id")
    CategoryResponseDTO toResponseDTO(Category category);

    List<CategoryResponseDTO> toResponseDTOList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "parent", ignore = true)
    @Mapping(target = "subcategories", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromDTO(CategoryDTO dto, @MappingTarget Category category);
} 