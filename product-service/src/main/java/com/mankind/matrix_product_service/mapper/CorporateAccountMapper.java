package com.mankind.matrix_product_service.mapper;

import com.mankind.api.product.dto.corporateaccount.CorporateAccountDTO;
import com.mankind.api.product.dto.corporateaccount.CorporateAccountResponseDTO;
import com.mankind.matrix_product_service.model.CorporateAccount;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CorporateAccountMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "dateOfJoined", defaultExpression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "isActive", defaultValue = "true")
    CorporateAccount toEntity(CorporateAccountDTO dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget CorporateAccount account, CorporateAccountDTO dto);

    CorporateAccountResponseDTO toResponseDTO(CorporateAccount account);
}
