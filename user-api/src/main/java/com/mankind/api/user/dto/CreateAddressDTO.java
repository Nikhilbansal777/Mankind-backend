package com.mankind.api.user.dto;

import com.mankind.api.user.enums.AddressType;
import lombok.Data;

@Data
public class CreateAddressDTO {
    private AddressType addressType;
    private Boolean isDefault = false;
    private String streetAddress;
    private String city;
    private String state;
    private String postalCode;
    private String country;
}