package com.mankind.mankindmatrixuserservice.service;

import com.mankind.api.user.dto.AddressDTO;
import com.mankind.api.user.dto.CreateAddressDTO;
import com.mankind.api.user.dto.UpdateAddressDTO;
import com.mankind.mankindmatrixuserservice.exception.AddressNotFoundException;
import com.mankind.mankindmatrixuserservice.exception.UserNotFoundException;
import com.mankind.mankindmatrixuserservice.mapper.AddressMapper;
import com.mankind.mankindmatrixuserservice.model.Address;
import com.mankind.api.user.enums.AddressType;
import com.mankind.mankindmatrixuserservice.model.User;
import com.mankind.mankindmatrixuserservice.repository.AddressRepository;
import com.mankind.mankindmatrixuserservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final AddressMapper addressMapper;

    @Autowired
    public AddressService(AddressRepository addressRepository, UserRepository userRepository, AddressMapper addressMapper) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
        this.addressMapper = addressMapper;
    }

    public List<AddressDTO> getAddressesByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        return addressRepository.findByUser(user).stream()
                .map(addressMapper::toDto)
                .collect(Collectors.toList());
    }

    public AddressDTO getAddressById(Long addressId) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address with ID " + addressId + " not found"));

        return addressMapper.toDto(address);
    }

    @Transactional
    public AddressDTO createAddress(Long userId, CreateAddressDTO createAddressDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User with ID " + userId + " not found"));

        // If this is set as default, unset any existing default of the same type
        if (Boolean.TRUE.equals(createAddressDTO.getIsDefault())) {
            addressRepository.findByUserAndIsDefaultAndAddressType(user, true, createAddressDTO.getAddressType())
                    .ifPresent(existingDefault -> {
                        existingDefault.setIsDefault(false);
                        addressRepository.save(existingDefault);
                    });
        }

        Address address = addressMapper.toEntity(createAddressDTO, user);
        address = addressRepository.save(address);

        return addressMapper.toDto(address);
    }

    @Transactional
    public AddressDTO updateAddress(Long addressId, UpdateAddressDTO updateAddressDTO) {
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new AddressNotFoundException("Address with ID " + addressId + " not found"));

        // If this is set as default, unset any existing default of the same type
        if (Boolean.TRUE.equals(updateAddressDTO.getIsDefault())) {
            AddressType addressType = updateAddressDTO.getAddressType() != null
                    ? updateAddressDTO.getAddressType() 
                    : address.getAddressType();

            addressRepository.findByUserAndIsDefaultAndAddressType(address.getUser(), true, addressType)
                    .ifPresent(existingDefault -> {
                        if (!existingDefault.getId().equals(addressId)) {
                            existingDefault.setIsDefault(false);
                            addressRepository.save(existingDefault);
                        }
                    });
        }

        addressMapper.updateEntityFromDto(updateAddressDTO, address);
        address = addressRepository.save(address);

        return addressMapper.toDto(address);
    }

    @Transactional
    public void deleteAddress(Long addressId) {
        if (!addressRepository.existsById(addressId)) {
            throw new AddressNotFoundException("Address with ID " + addressId + " not found");
        }

        addressRepository.deleteById(addressId);
    }
}
