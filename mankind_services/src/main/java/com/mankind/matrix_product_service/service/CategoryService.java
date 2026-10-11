package com.mankind.matrix_product_service.service;


import com.mankind.api.product.dto.category.CategoryDTO;
import com.mankind.api.product.dto.category.CategoryResponseDTO;
import com.mankind.matrix_product_service.exception.DuplicateResourceException;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.model.Category;
import com.mankind.matrix_product_service.repository.CategoryRepository;
import com.mankind.matrix_product_service.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final RoleVerificationService roleVerificationService;

    public CategoryResponseDTO createCategory(CategoryDTO categoryDTO) {
        // Verify admin role for category creation
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        // Validate parent category if provided
        Category parentCategory = null;
        if (categoryDTO.getParentId() != null) {
            parentCategory = categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent Category", "id", categoryDTO.getParentId()));
        }

        // Check for duplicate names at the same level
        if (categoryDTO.getParentId() == null) {
            if (categoryRepository.existsByNameAndParentIdIsNull(categoryDTO.getName())) {
                throw new DuplicateResourceException("Category", "name", categoryDTO.getName());
            }
        } else {
            if (categoryRepository.existsByNameAndParentId(categoryDTO.getName(), categoryDTO.getParentId())) {
                throw new DuplicateResourceException("Category", "name", categoryDTO.getName());
            }
        }

        try {
            Category category = categoryMapper.toEntity(categoryDTO);
            category.setParent(parentCategory);
            return categoryMapper.toResponseDTO(categoryRepository.save(category));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create category: " + e.getMessage(), e);
        }
    }

    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        List<Category> rootCategories = categoryRepository.findByParentIdIsNull();
        return categoryMapper.toResponseDTOList(rootCategories);
    }

    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        return categoryMapper.toResponseDTO(category);
    }

    public CategoryResponseDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        // Verify admin role for category updates
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        Category existingCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (categoryDTO.getParentId() != null && !categoryDTO.getParentId().equals(id)) {
            categoryRepository.findById(categoryDTO.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category", "id", categoryDTO.getParentId()));
        }

        if (!existingCategory.getName().equals(categoryDTO.getName())) {
            if (categoryDTO.getParentId() == null) {
                if (categoryRepository.existsByNameAndParentIdIsNull(categoryDTO.getName())) {
                    throw new DuplicateResourceException("Category", "name", categoryDTO.getName());
                }
            } else {
                if (categoryRepository.existsByNameAndParentId(categoryDTO.getName(), categoryDTO.getParentId())) {
                    throw new DuplicateResourceException("Category", "name", categoryDTO.getName());
                }
            }
        }

        categoryMapper.updateEntityFromDTO(categoryDTO, existingCategory);
        
        if (categoryDTO.getParentId() != null) {
            existingCategory.setParent(categoryRepository.getReferenceById(categoryDTO.getParentId()));
        } else {
            existingCategory.setParent(null);
        }

        return categoryMapper.toResponseDTO(categoryRepository.save(existingCategory));
    }

    public void deleteCategory(Long id) {
        // Verify admin role for category deletion
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));

        if (!category.getSubcategories().isEmpty()) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }

        categoryRepository.delete(category);
    }
} 