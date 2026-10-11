package com.mankind.mankindmatrixuserservice.service;

import com.mankind.api.user.dto.AuthRequest;
import com.mankind.api.user.dto.AuthResponse;
import com.mankind.api.user.dto.ResetPasswordRequest;
import com.mankind.api.user.dto.UpdateUserDTO;
import com.mankind.api.user.dto.UserDTO;
import com.mankind.api.user.dto.UserRegistrationDTO;
import com.mankind.api.user.enums.Role;
import com.mankind.mankindmatrixuserservice.exception.UserNotFoundException;
import com.mankind.mankindmatrixuserservice.mapper.UserMapper;
import com.mankind.mankindmatrixuserservice.mapper.UserRegistrationMapper;
import com.mankind.mankindmatrixuserservice.mapper.UserUpdateMapper;
import com.mankind.mankindmatrixuserservice.model.TokenResponse;
import com.mankind.mankindmatrixuserservice.model.User;
import com.mankind.mankindmatrixuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRegistrationMapper userRegistrationMapper;

    @Mock
    private UserUpdateMapper userUpdateMapper;

    @Mock
    private KeycloakAdminClientService kcAdmin;

    @Mock
    private KeycloakTokenService tokenService;

    @Mock
    private PasswordValidationService passwordValidationService;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(
                userRepository,
                userMapper,
                userRegistrationMapper,
                userUpdateMapper,
                kcAdmin,
                tokenService,
                passwordValidationService
        );
    }

    @Test
    void register_ShouldCreateUserSuccessfully() {
        UserRegistrationDTO registrationDTO = mock(UserRegistrationDTO.class);
        User user = createUser();
        User savedUser = createUser();
        UserDTO expectedDTO = mock(UserDTO.class);

        when(registrationDTO.getUsername()).thenReturn("yaswanth");
        when(registrationDTO.getEmail()).thenReturn("yaswanth@example.com");
        when(registrationDTO.getPassword()).thenReturn("Password@123");
        when(registrationDTO.getFirstName()).thenReturn("Venkata");
        when(registrationDTO.getLastName()).thenReturn("Yaswanth");
        when(registrationDTO.getCustomAttributes()).thenReturn(Map.of("department", "IT"));

        when(userRepository.existsByUsername("yaswanth")).thenReturn(false);
        when(userRepository.existsByEmail("yaswanth@example.com")).thenReturn(false);
        when(kcAdmin.createUser(
                "yaswanth",
                "yaswanth@example.com",
                "Password@123",
                "Venkata",
                "Yaswanth",
                Map.of("department", "IT")
        )).thenReturn("keycloak-123");

        when(userRegistrationMapper.toEntity(registrationDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(expectedDTO);

        UserDTO result = userService.register(registrationDTO);

        assertSame(expectedDTO, result);
        assertEquals("keycloak-123", user.getKeycloakId());
        assertEquals(Role.USER, user.getRole());
        assertTrue(user.isActive());

        verify(passwordValidationService).validatePassword("Password@123");
        verify(userRepository).save(user);
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        UserRegistrationDTO registrationDTO = mock(UserRegistrationDTO.class);

        when(registrationDTO.getPassword()).thenReturn("Password@123");
        when(registrationDTO.getUsername()).thenReturn("yaswanth");
        when(userRepository.existsByUsername("yaswanth")).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.register(registrationDTO)
        );

        assertEquals("Username already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(kcAdmin);
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        UserRegistrationDTO registrationDTO = mock(UserRegistrationDTO.class);

        when(registrationDTO.getPassword()).thenReturn("Password@123");
        when(registrationDTO.getUsername()).thenReturn("yaswanth");
        when(registrationDTO.getEmail()).thenReturn("yaswanth@example.com");

        when(userRepository.existsByUsername("yaswanth")).thenReturn(false);
        when(userRepository.existsByEmail("yaswanth@example.com")).thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.register(registrationDTO)
        );

        assertEquals("Email already in use", exception.getMessage());
        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(kcAdmin);
    }

    @Test
    void authenticate_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        AuthRequest authRequest = mock(AuthRequest.class);
        TokenResponse tokenResponse = mock(TokenResponse.class);

        when(authRequest.getUsername()).thenReturn("yaswanth");
        when(authRequest.getPassword()).thenReturn("Password@123");

        when(tokenResponse.getAccessToken()).thenReturn("access-token");
        when(tokenResponse.getRefreshToken()).thenReturn("refresh-token");
        when(tokenResponse.getExpiresIn()).thenReturn(3600L);

        when(tokenService.getToken("yaswanth", "Password@123"))
                .thenReturn(Mono.just(tokenResponse));

        AuthResponse result = userService.authenticate(authRequest).block();

        assertNotNull(result);
        assertEquals("access-token", result.getAccessToken());
        assertEquals("refresh-token", result.getRefreshToken());
        assertEquals(3600L, result.getExpiresIn());
    }

    @Test
    void authenticate_ShouldThrowUnauthorized_WhenAccessTokenIsMissing() {
        AuthRequest authRequest = mock(AuthRequest.class);
        TokenResponse tokenResponse = mock(TokenResponse.class);

        when(authRequest.getUsername()).thenReturn("yaswanth");
        when(authRequest.getPassword()).thenReturn("wrong-password");
        when(tokenResponse.getAccessToken()).thenReturn(null);

        when(tokenService.getToken("yaswanth", "wrong-password"))
                .thenReturn(Mono.just(tokenResponse));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.authenticate(authRequest).block()
        );

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatusCode());
        assertEquals("Invalid credentials", exception.getReason());
    }

    @Test
    void logout_ShouldRevokeRefreshToken() {
        when(tokenService.revokeRefreshToken("refresh-token"))
                .thenReturn(Mono.empty());

        userService.logout("refresh-token").block();

        verify(tokenService).revokeRefreshToken("refresh-token");
    }

    @Test
    void resetPassword_ShouldResetPasswordSuccessfully() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);
        User user = createUser();

        when(request.getUsername()).thenReturn("yaswanth");
        when(request.getNewPassword()).thenReturn("NewPassword@123");
        when(request.getTemporary()).thenReturn(true);
        when(userRepository.findByUsername("yaswanth"))
                .thenReturn(Optional.of(user));

        userService.resetPassword(request);

        verify(passwordValidationService)
                .validatePassword("NewPassword@123");

        verify(kcAdmin).resetPassword(
                "keycloak-123",
                "NewPassword@123",
                true
        );
    }

    @Test
    void resetPassword_ShouldThrowBadRequest_WhenUsernameIsBlank() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);

        when(request.getUsername()).thenReturn(" ");

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> userService.resetPassword(request)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        assertEquals("Username is required", exception.getReason());

        verifyNoInteractions(passwordValidationService);
        verifyNoInteractions(kcAdmin);
    }

    @Test
    void resetPassword_ShouldThrowException_WhenUserDoesNotExist() {
        ResetPasswordRequest request = mock(ResetPasswordRequest.class);

        when(request.getUsername()).thenReturn("unknown");
        when(request.getNewPassword()).thenReturn("NewPassword@123");
        when(userRepository.findByUsername("unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.resetPassword(request)
        );

        verify(passwordValidationService)
                .validatePassword("NewPassword@123");

        verifyNoInteractions(kcAdmin);
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        User user = createUser();
        UserDTO expectedDTO = mock(UserDTO.class);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(expectedDTO);

        UserDTO result = userService.getUserById(1L);

        assertSame(expectedDTO, result);
        verify(userMapper).toDto(user);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserDoesNotExist() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(99L)
        );

        assertEquals("User with ID 99 not found", exception.getMessage());
    }

    @Test
    void getAllUsers_ShouldReturnMappedPage() {
        Pageable pageable = mock(Pageable.class);

        User firstUser = createUser();
        User secondUser = createUser();
        secondUser.setId(2L);
        secondUser.setUsername("seconduser");

        UserDTO firstDTO = mock(UserDTO.class);
        UserDTO secondDTO = mock(UserDTO.class);

        Page<User> users = new PageImpl<>(List.of(firstUser, secondUser));

        when(userRepository.findAll(pageable)).thenReturn(users);
        when(userMapper.toDto(firstUser)).thenReturn(firstDTO);
        when(userMapper.toDto(secondUser)).thenReturn(secondDTO);

        Page<UserDTO> result = userService.getAllUsers(pageable);

        assertEquals(2, result.getTotalElements());
        assertEquals(List.of(firstDTO, secondDTO), result.getContent());
    }

    @Test
    void getUsersByIds_ShouldReturnMappedUsers() {
        User firstUser = createUser();
        User secondUser = createUser();
        secondUser.setId(2L);

        UserDTO firstDTO = mock(UserDTO.class);
        UserDTO secondDTO = mock(UserDTO.class);

        when(userRepository.findAllById(List.of(1L, 2L)))
                .thenReturn(List.of(firstUser, secondUser));

        when(userMapper.toDto(firstUser)).thenReturn(firstDTO);
        when(userMapper.toDto(secondUser)).thenReturn(secondDTO);

        List<UserDTO> result = userService.getUsersByIds(List.of(1L, 2L));

        assertEquals(2, result.size());
        assertEquals(List.of(firstDTO, secondDTO), result);
    }

    @Test
    void updateUser_ShouldUpdateUserSuccessfully() {
        User existingUser = createUser();
        UpdateUserDTO updateDTO = mock(UpdateUserDTO.class);
        UpdateUserDTO expectedDTO = mock(UpdateUserDTO.class);

        Map<String, String> customAttributes =
                Map.of("department", "Engineering");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(updateDTO.getFirstName()).thenReturn("UpdatedFirstName");
        when(updateDTO.getLastName()).thenReturn("UpdatedLastName");
        when(updateDTO.getEmail()).thenReturn("updated@example.com");
        when(updateDTO.getUsername()).thenReturn("updateduser");
        when(updateDTO.getRole()).thenReturn(Role.USER);
        when(updateDTO.getCustomAttributes()).thenReturn(customAttributes);
        when(updateDTO.getProfilePictureUrl())
                .thenReturn("https://example.com/profile.jpg");

        when(userRepository.existsByEmail("updated@example.com"))
                .thenReturn(false);
        when(userRepository.existsByUsername("updateduser"))
                .thenReturn(false);

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        when(userUpdateMapper.toDto(existingUser))
                .thenReturn(expectedDTO);

        UpdateUserDTO result = userService.updateUser(1L, updateDTO);

        assertSame(expectedDTO, result);
        assertEquals("UpdatedFirstName", existingUser.getFirstName());
        assertEquals("UpdatedLastName", existingUser.getLastName());
        assertEquals("updated@example.com", existingUser.getEmail());
        assertEquals("updateduser", existingUser.getUsername());
        assertEquals(Role.USER, existingUser.getRole());
        assertEquals(customAttributes, existingUser.getCustomAttributes());
        assertEquals(
                "https://example.com/profile.jpg",
                existingUser.getProfilePictureUrl()
        );

        verify(kcAdmin).updateUserProfile(
                "keycloak-123",
                "UpdatedFirstName",
                "UpdatedLastName",
                "updated@example.com"
        );
    }

    @Test
    void updateUser_ShouldThrowException_WhenEmailAlreadyExists() {
        User existingUser = createUser();
        UpdateUserDTO updateDTO = mock(UpdateUserDTO.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(updateDTO.getEmail()).thenReturn("duplicate@example.com");
        when(userRepository.existsByEmail("duplicate@example.com"))
                .thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.updateUser(1L, updateDTO)
        );

        assertEquals("Email already in use", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(kcAdmin);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUsernameAlreadyExists() {
        User existingUser = createUser();
        UpdateUserDTO updateDTO = mock(UpdateUserDTO.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(updateDTO.getUsername()).thenReturn("duplicateuser");
        when(userRepository.existsByUsername("duplicateuser"))
                .thenReturn(true);

        DataIntegrityViolationException exception = assertThrows(
                DataIntegrityViolationException.class,
                () -> userService.updateUser(1L, updateDTO)
        );

        assertEquals("Username already in use", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(kcAdmin);
    }

    @Test
    void updateUser_ShouldThrowException_WhenUserDoesNotExist() {
        UpdateUserDTO updateDTO = mock(UpdateUserDTO.class);

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(99L, updateDTO)
        );

        assertEquals("User with ID 99 not found", exception.getMessage());

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_ShouldNotUpdateKeycloak_WhenOnlyProfilePictureChanges() {
        User existingUser = createUser();
        UpdateUserDTO updateDTO = mock(UpdateUserDTO.class);
        UpdateUserDTO expectedDTO = mock(UpdateUserDTO.class);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(updateDTO.getProfilePictureUrl())
                .thenReturn("https://example.com/new-profile.jpg");

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        when(userUpdateMapper.toDto(existingUser))
                .thenReturn(expectedDTO);

        UpdateUserDTO result = userService.updateUser(1L, updateDTO);

        assertSame(expectedDTO, result);
        assertEquals(
                "https://example.com/new-profile.jpg",
                existingUser.getProfilePictureUrl()
        );

        verify(kcAdmin, never()).updateUserProfile(
                anyString(),
                anyString(),
                anyString(),
                anyString()
        );
    }

    private User createUser() {
        User user = new User();
        user.setId(1L);
        user.setKeycloakId("keycloak-123");
        user.setUsername("yaswanth");
        user.setRole(Role.USER);
        user.setFirstName("Venkata");
        user.setLastName("Yaswanth");
        user.setEmail("yaswanth@example.com");
        user.setCustomAttributes(Map.of("department", "IT"));
        user.setActive(true);
        user.setProfilePictureUrl("https://example.com/old-profile.jpg");
        return user;
    }
}
