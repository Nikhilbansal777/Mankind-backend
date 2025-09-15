package com.mankind.api.user.dto;

import com.mankind.api.user.enums.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateAddressDTO {
    @NotNull
    private AddressType addressType;
    private Boolean isDefault = false;
    @NotBlank
    @Size(max = 255)
    private String streetAddress;
    @NotBlank
    @Size(max = 100)
    private String city;
    @NotBlank
    @Size(max = 100)
    private String state;
    @NotBlank
    @Size(max = 20)
    private String postalCode;
    @NotBlank
    @Size(max = 100)
    private String country;
}