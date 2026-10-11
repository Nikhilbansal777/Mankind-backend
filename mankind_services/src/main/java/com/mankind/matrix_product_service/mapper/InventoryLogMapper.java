package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.inventory.InventoryLogDTO;
import com.mankind.matrix_product_service.model.InventoryLog;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface InventoryLogMapper {
    @Mapping(target = "inventoryId", source = "inventory.id")
    @Mapping(target = "action", source = "actionType")
    @Mapping(target = "quantity", source = "quantity", numberFormat = "#.##")
    InventoryLogDTO toDTO(InventoryLog inventoryLog);

    @Mapping(target = "inventory", ignore = true)
    @Mapping(target = "actionType", source = "action")
    InventoryLog toEntity(InventoryLogDTO dto);

    default String mapActionType(InventoryLog.InventoryActionType actionType) {
        return actionType != null ? actionType.name() : null;
    }

    default InventoryLog.InventoryActionType mapAction(String action) {
        return action != null ? InventoryLog.InventoryActionType.valueOf(action) : null;
    }
} 