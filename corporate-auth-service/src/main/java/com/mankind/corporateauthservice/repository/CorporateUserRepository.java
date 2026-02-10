package com.mankind.corporateauthservice.repository;

import com.mankind.corporateauthservice.model.CorporateUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CorporateUserRepository extends JpaRepository<CorporateUser, Long> {
    Optional<CorporateUser> findByEmail(String email);
    boolean existsByEmail(String email);
}
