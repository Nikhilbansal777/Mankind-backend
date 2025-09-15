package com.mankind.api.user.dto;

import com.mankind.api.user.enums.AddressType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateAddressDTO {
    private AddressType addressType;
    private Boolean isDefault;
    @Size(max = 255)
    private String streetAddress;
    @Size(max = 100)
    private String city;
    @Size(max = 100)
    private String state;
    @Size(max = 20)
    private String postalCode;
    @Size(max = 100)
    private String country;
}