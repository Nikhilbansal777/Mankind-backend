package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.supplier.SupplierDTO;
import com.mankind.api.product.dto.supplier.SupplierResponseDTO;
import com.mankind.api.product.dto.supplier.SupplierDashboardDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.SupplierMapper;
import com.mankind.matrix_product_service.model.Supplier;
import com.mankind.matrix_product_service.repository.SupplierRepository;
import com.mankind.matrix_product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {
    private final SupplierRepository supplierRepository;
    private final SupplierMapper supplierMapper;
    private final ProductRepository productRepository;

    @Transactional
    public SupplierResponseDTO createSupplier(SupplierDTO supplierDTO) {
        log.info("Creating new supplier: {}", supplierDTO.getName());
        
        // Validate supplier name uniqueness
        validateSupplierName(supplierDTO.getName(), null);
        
        Supplier supplier = supplierMapper.toEntity(supplierDTO);
        Supplier savedSupplier = supplierRepository.save(supplier);
        
        log.info("Supplier created successfully with ID: {}", savedSupplier.getId());
        return supplierMapper.toResponseDTO(savedSupplier);
    }

    @Transactional(readOnly = true)
    public Page<SupplierResponseDTO> getAllSuppliers(Pageable pageable) {
        return supplierRepository.findByIsActiveTrue(pageable)
                .map(supplierMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public SupplierResponseDTO getSupplierById(Long id) {
        return supplierRepository.findByIdAndIsActiveTrue(id)
                .map(supplierMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
    }

    @Transactional
    public SupplierResponseDTO updateSupplier(Long id, SupplierDTO supplierDTO) {
        log.info("Updating supplier with ID: {}", id);
        
        Supplier supplier = supplierRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        
        // Validate supplier name uniqueness
        if (supplierDTO.getName() != null && !supplierDTO.getName().equals(supplier.getName())) {
            validateSupplierName(supplierDTO.getName(), id);
        }
        
        supplierMapper.updateEntity(supplier, supplierDTO);
        Supplier updatedSupplier = supplierRepository.save(supplier);
        
        log.info("Supplier updated successfully with ID: {}", updatedSupplier.getId());
        return supplierMapper.toResponseDTO(updatedSupplier);
    }

    @Transactional
    public void deleteSupplier(Long id) {
        log.info("Deleting supplier with ID: {}", id);
        
        Supplier supplier = supplierRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));
        
        supplier.setActive(false);
        supplierRepository.save(supplier);
        
        log.info("Supplier marked as inactive with ID: {}", id);
    }

    @Transactional(readOnly = true)
    public SupplierDashboardDTO getSupplierDashboard(Long id) {
        Supplier supplier = supplierRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with ID: " + id));

        long total = productRepository.countBySuppliers_Id(id);
        long active = productRepository.countBySuppliers_IdAndIsActiveTrue(id);
        long inactive = productRepository.countBySuppliers_IdAndIsActiveFalse(id);

        return SupplierDashboardDTO.builder()
                .supplierId(supplier.getId())
                .supplierName(supplier.getName())
                .totalProducts(total)
                .activeProducts(active)
                .inactiveProducts(inactive)
                .createdAt(supplier.getCreatedAt())
                .updatedAt(supplier.getUpdatedAt())
                .build();
    }

    private void validateSupplierName(String name, Long supplierId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier name cannot be empty");
        }
        
        boolean exists = supplierId == null 
                ? supplierRepository.existsByName(name)
                : supplierRepository.existsByNameAndIdNot(name, supplierId);
                
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Supplier with name '" + name + "' already exists");
        }
    }
}