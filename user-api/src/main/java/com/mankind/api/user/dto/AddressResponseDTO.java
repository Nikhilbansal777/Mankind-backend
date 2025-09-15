package com.mankind.api.user.dto;

import com.mankind.api.user.enums.AddressType;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for exposing address information.
 */
@Data
public class AddressResponseDTO {
    private Long id;
    private Long userId;
    private AddressType addressType;
    private Boolean isDefault;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
