package com.mankind.mankindmatrixuserservice.repository;

import com.mankind.mankindmatrixuserservice.model.Address;
import com.mankind.api.user.enums.AddressType;
import com.mankind.mankindmatrixuserservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUser(User user);
    List<Address> findByUserAndAddressType(User user, AddressType addressType);
    Optional<Address> findByUserAndIsDefaultAndAddressType(User user, Boolean isDefault, AddressType addressType);
}