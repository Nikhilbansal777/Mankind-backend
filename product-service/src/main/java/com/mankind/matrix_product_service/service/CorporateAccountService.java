package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.corporateaccount.CorporateAccountDTO;
import com.mankind.api.product.dto.corporateaccount.CorporateAccountResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.CorporateAccountMapper;
import com.mankind.matrix_product_service.model.CorporateAccount;
import com.mankind.matrix_product_service.repository.CorporateAccountRepository;
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
public class CorporateAccountService {
    private final CorporateAccountRepository corporateAccountRepository;
    private final CorporateAccountMapper corporateAccountMapper;

    @Transactional
    public CorporateAccountResponseDTO createCorporateAccount(CorporateAccountDTO corporateAccountDTO) {
        log.info("Creating new corporate account: {}", corporateAccountDTO.getName());
        
        // Validate corporate account name uniqueness
        validateCorporateAccountName(corporateAccountDTO.getName(), null);
        
        CorporateAccount account = corporateAccountMapper.toEntity(corporateAccountDTO);
        CorporateAccount savedAccount = corporateAccountRepository.save(account);
        
        log.info("Corporate account created successfully with ID: {}", savedAccount.getId());
        return corporateAccountMapper.toResponseDTO(savedAccount);
    }

    @Transactional(readOnly = true)
    public Page<CorporateAccountResponseDTO> getAllCorporateAccounts(Pageable pageable) {
        log.info("Fetching all active corporate accounts");
        return corporateAccountRepository.findByIsActiveTrue(pageable)
                .map(corporateAccountMapper::toResponseDTO);
    }

    @Transactional(readOnly = true)
    public CorporateAccountResponseDTO getCorporateAccountById(Long id) {
        log.info("Fetching corporate account with ID: {}", id);
        return corporateAccountRepository.findByIdAndIsActiveTrue(id)
                .map(corporateAccountMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate account not found with ID: " + id));
    }

    @Transactional
    public CorporateAccountResponseDTO updateCorporateAccount(Long id, CorporateAccountDTO corporateAccountDTO) {
        log.info("Updating corporate account with ID: {}", id);
        
        CorporateAccount account = corporateAccountRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate account not found with ID: " + id));
        
        // Validate corporate account name uniqueness
        if (corporateAccountDTO.getName() != null && !corporateAccountDTO.getName().equals(account.getName())) {
            validateCorporateAccountName(corporateAccountDTO.getName(), id);
        }
        
        corporateAccountMapper.updateEntity(account, corporateAccountDTO);
        CorporateAccount updatedAccount = corporateAccountRepository.save(account);
        
        log.info("Corporate account updated successfully with ID: {}", updatedAccount.getId());
        return corporateAccountMapper.toResponseDTO(updatedAccount);
    }

    @Transactional
    public void deleteCorporateAccount(Long id) {
        log.info("Deleting corporate account with ID: {}", id);
        
        CorporateAccount account = corporateAccountRepository.findByIdAndIsActiveTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Corporate account not found with ID: " + id));
        
        account.setActive(false);
        corporateAccountRepository.save(account);
        
        log.info("Corporate account marked as inactive with ID: {}", id);
    }

    private void validateCorporateAccountName(String name, Long accountId) {
        if (name == null || name.trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Corporate account name cannot be empty");
        }
        
        boolean exists = accountId == null 
                ? corporateAccountRepository.existsByName(name)
                : corporateAccountRepository.existsByNameAndIdNot(name, accountId);
                
        if (exists) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Corporate account with name '" + name + "' already exists");
        }
    }
}
