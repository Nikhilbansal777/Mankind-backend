package com.mankind.matrix_product_service.service;


import com.mankind.api.product.dto.inventory.InventoryDTO;
import com.mankind.api.product.dto.inventory.InventoryLogDTO;
import com.mankind.api.product.dto.inventory.InventoryResponseDTO;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import com.mankind.matrix_product_service.mapper.InventoryLogMapper;
import com.mankind.matrix_product_service.mapper.InventoryMapper;
import com.mankind.matrix_product_service.model.Inventory;
import com.mankind.matrix_product_service.model.InventoryLog;
import com.mankind.matrix_product_service.model.InventoryLog.InventoryActionType;
import com.mankind.matrix_product_service.repository.InventoryLogRepository;
import com.mankind.matrix_product_service.repository.InventoryRepository;
import com.mankind.matrix_product_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final ProductRepository productRepository;
    private final InventoryMapper inventoryMapper;
    private final InventoryLogMapper inventoryLogMapper;
    private final RoleVerificationService roleVerificationService;

    @Transactional
    public InventoryResponseDTO createInventory(Long productId, InventoryDTO inventoryDTO) {
        // Verify admin role for inventory creation
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("Product not found with id: " + productId);
        }

        if (inventoryRepository.existsByProductId(productId)) {
            throw new IllegalStateException("Inventory already exists for this product");
        }

        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);
        inventory.setProduct(productRepository.getReferenceById(productId));
        inventory.setActive(true);
        inventory = inventoryRepository.save(inventory);

        // Create inventory log for initial stock
        InventoryLog log = InventoryLog.builder()
            .inventory(inventory)
            .actionType(InventoryActionType.RESTOCK)
            .quantity(inventoryDTO.getAvailableQuantity())
            .description("Initial stock created")
            .createdBy("SYSTEM")
            .build();
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    public InventoryResponseDTO getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId)
                .map(inventoryMapper::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));
    }

    @Transactional
    public InventoryResponseDTO updateInventory(Long productId, InventoryDTO inventoryDTO) {
        // Verify admin role for inventory updates
        roleVerificationService.verifyAdminOrSuperAdminRole();
        
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        // Log price changes if price is updated
        if (inventoryDTO.getPrice() != null && !inventoryDTO.getPrice().equals(inventory.getPrice())) {
            InventoryLog priceLog = InventoryLog.builder()
                .inventory(inventory)
                .actionType(InventoryActionType.PRICE_CHANGE)
                .quantity(BigDecimal.ZERO)
                .description(String.format("Price updated from %s to %s", 
                    formatPriceDisplay(inventory.getPrice(), inventory.getCurrency()),
                    formatPriceDisplay(inventoryDTO.getPrice(), inventoryDTO.getCurrency())))
                .createdBy("SYSTEM")
                .build();
            inventoryLogRepository.save(priceLog);
        }

        // Log quantity changes if quantity is updated
        if (inventoryDTO.getAvailableQuantity() != null && 
            !inventoryDTO.getAvailableQuantity().equals(inventory.getAvailableQuantity())) {
            BigDecimal quantityDiff = inventoryDTO.getAvailableQuantity().subtract(inventory.getAvailableQuantity());
            InventoryLog quantityLog = InventoryLog.builder()
                .inventory(inventory)
                .actionType(quantityDiff.compareTo(BigDecimal.ZERO) > 0 ? 
                    InventoryActionType.RESTOCK : InventoryActionType.STOCK_ADJUSTMENT)
                .quantity(quantityDiff.abs())
                .description(quantityDiff.compareTo(BigDecimal.ZERO) > 0 ? 
                    "Stock adjusted (added)" : "Stock adjusted (removed)")
                .createdBy("SYSTEM")
                .build();
            inventoryLogRepository.save(quantityLog);
        }

        inventoryMapper.updateEntity(inventory, inventoryDTO);
        inventory = inventoryRepository.save(inventory);

        return inventoryMapper.toResponseDTO(inventory);
    }

    private String formatPriceDisplay(BigDecimal price, String currency) {
        if (price == null || currency == null) {
            return "N/A";
        }
        return String.format("%s %.2f", currency, price);
    }

    @Transactional
    public void deleteInventory(Long productId) {
        // Verify admin role for inventory deletion
        roleVerificationService.verifyAdminOrSuperAdminRole();
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setActive(false);
        inventoryRepository.save(inventory);
    }

    @Transactional
    public InventoryResponseDTO addStock(Long productId, BigDecimal quantity) {
        // Verify admin role for adding stock
        roleVerificationService.verifyAdminOrSuperAdminRole();
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log
        InventoryLog log = new InventoryLog();
        log.setInventory(inventory);
        log.setActionType(InventoryActionType.RESTOCK);
        log.setQuantity(quantity);
        log.setDescription("Stock added to inventory");
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO removeStock(Long productId, BigDecimal quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient stock available");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log
        InventoryLog log = new InventoryLog();
        log.setInventory(inventory);
        log.setActionType(InventoryActionType.SALE);
        log.setQuantity(quantity);
        log.setDescription("Stock removed from inventory");
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO reserveStock(Long productId, BigDecimal quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient stock available for reservation");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        inventory.setReservedQuantity(inventory.getReservedQuantity().add(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log
        InventoryLog log = new InventoryLog();
        log.setInventory(inventory);
        log.setActionType(InventoryActionType.RESERVATION);
        log.setQuantity(quantity);
        log.setDescription("Stock reserved");
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO unreserveStock(Long productId, BigDecimal quantity) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient reserved stock");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(quantity));
        inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log
        InventoryLog log = new InventoryLog();
        log.setInventory(inventory);
        log.setActionType(InventoryActionType.UNRESERVATION);
        log.setQuantity(quantity);
        log.setDescription("Stock reservation removed");
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    public List<InventoryLogDTO> getInventoryLogs(Long productId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        return inventoryLogRepository.findByInventoryId(inventory.getId())
                .stream()
                .map(inventoryLogMapper::toDTO)
                .toList();
    }

    @Transactional
    public InventoryResponseDTO reserveStockForCart(Long productId, BigDecimal quantity, Long userId, Long cartId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getAvailableQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient stock available for cart reservation");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantity));
        inventory.setReservedQuantity(inventory.getReservedQuantity().add(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log for cart reservation
        InventoryLog log = InventoryLog.builder()
            .inventory(inventory)
            .actionType(InventoryActionType.CART_ADD)
            .quantity(quantity)
            .description("Stock reserved for cart")
            .createdBy("CART_SERVICE")
            .userId(userId)
            .cartId(cartId)
            .build();
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO unreserveStockForCart(Long productId, BigDecimal quantity, Long userId, Long cartId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient reserved stock for cart unreservation");
        }

        inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(quantity));
        inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log for cart unreservation
        InventoryLog log = InventoryLog.builder()
            .inventory(inventory)
            .actionType(InventoryActionType.CART_REMOVE)
            .quantity(quantity)
            .description("Stock reservation removed from cart")
            .createdBy("CART_SERVICE")
            .userId(userId)
            .cartId(cartId)
            .build();
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO updateReservedStockForCart(Long productId, BigDecimal oldQuantity, BigDecimal newQuantity, Long userId, Long cartId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        BigDecimal quantityDiff = newQuantity.subtract(oldQuantity);

        if (quantityDiff.compareTo(BigDecimal.ZERO) > 0) {
            // Adding more items to cart
            if (inventory.getAvailableQuantity().compareTo(quantityDiff) < 0) {
                throw new IllegalStateException("Insufficient stock available for cart update");
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity().subtract(quantityDiff));
            inventory.setReservedQuantity(inventory.getReservedQuantity().add(quantityDiff));
        } else if (quantityDiff.compareTo(BigDecimal.ZERO) < 0) {
            // Removing items from cart
            BigDecimal absDiff = quantityDiff.abs();
            if (inventory.getReservedQuantity().compareTo(absDiff) < 0) {
                throw new IllegalStateException("Insufficient reserved stock for cart update");
            }
            inventory.setAvailableQuantity(inventory.getAvailableQuantity().add(absDiff));
            inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(absDiff));
        }

        inventory = inventoryRepository.save(inventory);

        // Create inventory log for cart update
        InventoryLog log = InventoryLog.builder()
            .inventory(inventory)
            .actionType(InventoryActionType.CART_UPDATE)
            .quantity(quantityDiff.abs())
            .description(String.format("Cart quantity updated from %s to %s", oldQuantity, newQuantity))
            .createdBy("CART_SERVICE")
            .userId(userId)
            .cartId(cartId)
            .build();
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }

    @Transactional
    public InventoryResponseDTO convertReservedToSold(Long productId, BigDecimal quantity, Long orderId) {
        Inventory inventory = inventoryRepository.findByProductId(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + productId));

        if (inventory.getReservedQuantity().compareTo(quantity) < 0) {
            throw new IllegalStateException("Insufficient reserved stock to convert to sold");
        }

        // Move quantity from reserved to sold
        inventory.setReservedQuantity(inventory.getReservedQuantity().subtract(quantity));
        inventory.setSoldQuantity(inventory.getSoldQuantity().add(quantity));
        inventory = inventoryRepository.save(inventory);

        // Create inventory log for cart conversion
        InventoryLog log = InventoryLog.builder()
            .inventory(inventory)
            .actionType(InventoryActionType.CART_CONVERT)
            .quantity(quantity)
            .description("Stock converted from reserved to sold for order")
            .createdBy("CART_SERVICE")
            .orderId(orderId)
            .build();
        inventoryLogRepository.save(log);

        return inventoryMapper.toResponseDTO(inventory);
    }
} 