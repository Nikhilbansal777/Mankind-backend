package com.mankind.corporateauthservice.service;

import com.mankind.corporateauthservice.dto.CorporateAuthResponse;
import com.mankind.corporateauthservice.dto.CorporateLoginRequest;
import com.mankind.corporateauthservice.dto.CorporateRegistrationRequest;
import com.mankind.corporateauthservice.dto.CorporateUserResponse;
import com.mankind.corporateauthservice.model.CorporateUser;
import com.mankind.corporateauthservice.repository.CorporateUserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;

@Service
public class CorporateAuthService {

    private final CorporateUserRepository corporateUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public CorporateAuthService(CorporateUserRepository corporateUserRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.corporateUserRepository = corporateUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public CorporateAuthResponse register(CorporateRegistrationRequest request) {
        if (request.getDateOfJoining().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Date of joining cannot be in the future");
        }
        if (corporateUserRepository.existsByEmail(request.getEmail())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already in use");
        }

        CorporateUser user = new CorporateUser();
        user.setCorporateName(request.getCorporateName());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setDateOfJoining(request.getDateOfJoining());

        CorporateUser savedUser = corporateUserRepository.save(user);

        String token = jwtService.generateToken(savedUser);

        return CorporateAuthResponse.builder()
                .message("Corporate user registered")
                .accessToken(token)
                .expiresIn(jwtService.getExpiresInSeconds())
                .user(toResponse(savedUser))
                .build();
    }

    public CorporateAuthResponse login(CorporateLoginRequest request) {
        CorporateUser user = corporateUserRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!user.getCorporateName().equals(request.getCorporateName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (!user.getDateOfJoining().equals(request.getDateOfJoining())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        String token = jwtService.generateToken(user);

        return CorporateAuthResponse.builder()
                .message("Corporate login successful")
                .accessToken(token)
                .expiresIn(jwtService.getExpiresInSeconds())
                .user(toResponse(user))
                .build();
    }

    private CorporateUserResponse toResponse(CorporateUser user) {
        return CorporateUserResponse.builder()
                .id(user.getId())
                .corporateName(user.getCorporateName())
                .email(user.getEmail())
                .dateOfJoining(user.getDateOfJoining())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
