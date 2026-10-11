package com.mankind.mankindmatrixuserservice.mapper;

import com.mankind.api.user.dto.AddressDTO;
import com.mankind.api.user.dto.CreateAddressDTO;
import com.mankind.api.user.dto.UpdateAddressDTO;
import com.mankind.mankindmatrixuserservice.model.Address;
import com.mankind.mankindmatrixuserservice.model.User;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

    public AddressDTO toDto(Address address) {
        if (address == null) {
            return null;
        }

        AddressDTO dto = new AddressDTO();
        dto.setId(address.getId());
        dto.setUserId(address.getUser().getId());
        dto.setAddressType(address.getAddressType());
        dto.setIsDefault(address.getIsDefault());
        dto.setStreetAddress(address.getStreetAddress());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setPostalCode(address.getPostalCode());
        dto.setCountry(address.getCountry());
        dto.setCreatedAt(address.getCreatedAt());
        dto.setUpdatedAt(address.getUpdatedAt());
        
        return dto;
    }

    public Address toEntity(CreateAddressDTO dto, User user) {
        if (dto == null) {
            return null;
        }

        Address address = new Address();
        address.setUser(user);
        address.setAddressType(dto.getAddressType());
        address.setIsDefault(dto.getIsDefault());
        address.setStreetAddress(dto.getStreetAddress());
        address.setCity(dto.getCity());
        address.setState(dto.getState());
        address.setPostalCode(dto.getPostalCode());
        address.setCountry(dto.getCountry());
        
        return address;
    }

    public void updateEntityFromDto(UpdateAddressDTO dto, Address address) {
        if (dto == null) {
            return;
        }

        if (dto.getAddressType() != null) {
            address.setAddressType(dto.getAddressType());
        }
        if (dto.getIsDefault() != null) {
            address.setIsDefault(dto.getIsDefault());
        }
        if (dto.getStreetAddress() != null) {
            address.setStreetAddress(dto.getStreetAddress());
        }
        if (dto.getCity() != null) {
            address.setCity(dto.getCity());
        }
        if (dto.getState() != null) {
            address.setState(dto.getState());
        }
        if (dto.getPostalCode() != null) {
            address.setPostalCode(dto.getPostalCode());
        }
        if (dto.getCountry() != null) {
            address.setCountry(dto.getCountry());
        }
    }
}