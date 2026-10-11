package com.mankind.matrix_cart_service.mapper;

import com.mankind.matrix_cart_service.dto.CartDTO;
import com.mankind.matrix_cart_service.dto.CartResponseDTO;
import com.mankind.matrix_cart_service.model.Cart;
import com.mankind.matrix_cart_service.model.CartStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CartItemMapper.class})
public interface CartMapper {
    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "userId", source = "userId"),
        @Mapping(target = "sessionId", source = "sessionId"),
        @Mapping(target = "status", source = "status", qualifiedByName = "stringToCartStatus"),
        @Mapping(target = "cartItems", source = "items"),
        @Mapping(target = "subtotal", ignore = true),
        @Mapping(target = "tax", ignore = true),
        @Mapping(target = "total", ignore = true)
    })
    Cart toEntity(CartResponseDTO dto);

    @Mappings({
        @Mapping(target = "id", source = "id"),
        @Mapping(target = "userId", source = "userId"),
        @Mapping(target = "sessionId", source = "sessionId"),
        @Mapping(target = "status", source = "status", qualifiedByName = "cartStatusToString"),
        @Mapping(target = "items", source = "cartItems"),
        @Mapping(target = "subtotal", ignore = true),
        @Mapping(target = "tax", ignore = true),
        @Mapping(target = "total", ignore = true)
    })
    CartResponseDTO toResponseDTO(Cart entity);

    void updateEntityFromDTO(CartResponseDTO dto, @MappingTarget Cart entity);

    @Named("stringToCartStatus")
    default CartStatus stringToCartStatus(String status) {
        return status == null ? null : CartStatus.valueOf(status);
    }

    @Named("cartStatusToString")
    default String cartStatusToString(CartStatus status) {
        return status == null ? null : status.name();
    }
} 