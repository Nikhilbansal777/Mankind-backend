package com.mankind.api.user.dto;

import com.mankind.api.user.enums.AddressType;
import lombok.Data;

@Data
public class UpdateAddressDTO {
    private AddressType addressType;
    private Boolean isDefault;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}