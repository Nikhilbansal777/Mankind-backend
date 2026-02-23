package com.mankind.corporateauthservice.service;

import com.mankind.corporateauthservice.dto.CorporateAuthResponse;
import com.mankind.corporateauthservice.dto.CorporateLoginRequest;
import com.mankind.corporateauthservice.dto.CorporateRegistrationRequest;
import com.mankind.corporateauthservice.model.CorporateUser;
import com.mankind.corporateauthservice.repository.CorporateUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CorporateAuthServiceTest {

    @Mock
    private CorporateUserRepository corporateUserRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private CorporateAuthService corporateAuthService;

    @Captor
    private ArgumentCaptor<CorporateUser> userCaptor;

    private CorporateRegistrationRequest registrationRequest;
    private CorporateLoginRequest loginRequest;
    private CorporateUser storedUser;

    @BeforeEach
    void setUp() {
        registrationRequest = new CorporateRegistrationRequest();
        registrationRequest.setCorporateName("Acme Corp");
        registrationRequest.setEmail("admin@acme.com");
        registrationRequest.setPassword("SecurePassword123!");
        registrationRequest.setDateOfJoining(LocalDate.of(2024, 1, 15));

        loginRequest = new CorporateLoginRequest();
        loginRequest.setEmail("admin@acme.com");
        loginRequest.setPassword("SecurePassword123!");

        storedUser = new CorporateUser();
        storedUser.setId(1L);
        storedUser.setCorporateName("Acme Corp");
        storedUser.setEmail("admin@acme.com");
        storedUser.setPasswordHash("$bcrypt");
        storedUser.setDateOfJoining(LocalDate.of(2024, 1, 15));
        storedUser.setCreatedAt(LocalDateTime.of(2024, 1, 16, 10, 0));
        storedUser.setUpdatedAt(LocalDateTime.of(2024, 1, 20, 11, 0));
    }

    @Test
    void registerRejectsFutureDate() {
        registrationRequest.setDateOfJoining(LocalDate.now().plusDays(1));

        assertThatThrownBy(() -> corporateAuthService.register(registrationRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.BAD_REQUEST));
    }

    @Test
    void registerRejectsDuplicateEmail() {
        when(corporateUserRepository.existsByEmail(eq(registrationRequest.getEmail()))).thenReturn(true);

        assertThatThrownBy(() -> corporateAuthService.register(registrationRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.CONFLICT));
    }

    @Test
    void registerNormalizesEmailBeforePersistence() {
        registrationRequest.setEmail("  Admin@Acme.com ");
        when(corporateUserRepository.existsByEmail(eq("admin@acme.com"))).thenReturn(false);
        when(passwordEncoder.encode(eq(registrationRequest.getPassword()))).thenReturn("$encoded");
        when(corporateUserRepository.save(any(CorporateUser.class))).thenReturn(storedUser);
        when(jwtService.generateToken(eq(storedUser))).thenReturn("token");
        when(jwtService.getExpiresInSeconds()).thenReturn(3600L);

        corporateAuthService.register(registrationRequest);

        verify(corporateUserRepository).save(userCaptor.capture());
        CorporateUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("admin@acme.com");
    }

    @Test
    void registerCreatesUserAndReturnsToken() {
        when(corporateUserRepository.existsByEmail(eq(registrationRequest.getEmail()))).thenReturn(false);
        when(passwordEncoder.encode(eq(registrationRequest.getPassword()))).thenReturn("$encoded");
        when(corporateUserRepository.save(any(CorporateUser.class))).thenReturn(storedUser);
        when(jwtService.generateToken(eq(storedUser))).thenReturn("token");
        when(jwtService.getExpiresInSeconds()).thenReturn(3600L);

        CorporateAuthResponse response = corporateAuthService.register(registrationRequest);

        verify(corporateUserRepository).save(userCaptor.capture());
        CorporateUser savedUser = userCaptor.getValue();
        assertThat(savedUser.getEmail()).isEqualTo("admin@acme.com");
        assertThat(savedUser.getPasswordHash()).isEqualTo("$encoded");
        assertThat(response.getAccessToken()).isEqualTo("token");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getUser().getEmail()).isEqualTo("admin@acme.com");
    }

    @Test
    void loginRejectsUnknownEmail() {
        when(corporateUserRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> corporateAuthService.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void loginNormalizesEmailBeforeLookup() {
        loginRequest.setEmail("  ADMIN@ACME.COM ");
        when(corporateUserRepository.findByEmail(eq("admin@acme.com"))).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(eq(loginRequest.getPassword()), eq(storedUser.getPasswordHash()))).thenReturn(true);
        when(jwtService.generateToken(eq(storedUser))).thenReturn("token");
        when(jwtService.getExpiresInSeconds()).thenReturn(3600L);

        CorporateAuthResponse response = corporateAuthService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("token");
        verify(corporateUserRepository).findByEmail("admin@acme.com");
    }

    @Test
    void loginRejectsPasswordMismatch() {
        when(corporateUserRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(eq(loginRequest.getPassword()), eq(storedUser.getPasswordHash()))).thenReturn(false);

        assertThatThrownBy(() -> corporateAuthService.login(loginRequest))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> assertThat(((ResponseStatusException) ex).getStatusCode())
                        .isEqualTo(HttpStatus.UNAUTHORIZED));
    }

    @Test
    void loginReturnsTokenOnSuccess() {
        when(corporateUserRepository.findByEmail(eq(loginRequest.getEmail()))).thenReturn(Optional.of(storedUser));
        when(passwordEncoder.matches(eq(loginRequest.getPassword()), eq(storedUser.getPasswordHash()))).thenReturn(true);
        when(jwtService.generateToken(eq(storedUser))).thenReturn("token");
        when(jwtService.getExpiresInSeconds()).thenReturn(3600L);

        CorporateAuthResponse response = corporateAuthService.login(loginRequest);

        assertThat(response.getAccessToken()).isEqualTo("token");
        assertThat(response.getExpiresIn()).isEqualTo(3600L);
        assertThat(response.getUser().getId()).isEqualTo(1L);
        assertThat(response.getUser().getEmail()).isEqualTo("admin@acme.com");
    }
}
