package com.mankind.mankindmatrixuserservice.controller;

import com.mankind.api.user.dto.AuthRequest;
import com.mankind.api.user.dto.AuthResponse;
import com.mankind.api.user.dto.LogoutRequest;
import com.mankind.api.user.dto.ResetPasswordRequest;
import com.mankind.api.user.dto.UserDTO;
import com.mankind.api.user.dto.UserRegistrationDTO;
import com.mankind.mankindmatrixuserservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
@Tag(name = "🔐 Authentication", description = "Public API endpoints for user registration and authentication. These endpoints do not require authentication.")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(
        summary = "Register a new user", 
        description = """
            Creates a new user account in both Keycloak and the local database.
            
            ## Process:
            1. Validates input data (username, email, password)
            2. Creates user in Keycloak authentication system
            3. Creates user record in local database (without password)
            4. Returns the created user information (password is never returned)
            
            ## Requirements:
            - Username must be unique
            - Email must be unique and valid format
            - Password must meet security requirements
            - First name and last name are required
            
            ## Security:
            - Password is sent to Keycloak for secure storage
            - Password is never stored in local database
            - Password is never returned in API responses
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "User registration details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = UserRegistrationDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Basic Registration",
                        summary = "Basic user registration",
                        description = "Register a new user with basic information",
                        value = """
                            {
                                "username": "john.doe",
                                "email": "john.doe@example.com",
                                "password": "SecurePassword123!",
                                "firstName": "John",
                                "lastName": "Doe",
                                "customAttributes": {
                                    "phone": "+1234567890",
                                    "preferredLanguage": "en"
                                }
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "User registered successfully",
                content = @Content(
                    schema = @Schema(implementation = UserDTO.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "id": 1,
                                    "username": "john.doe",
                                    "email": "john.doe@example.com",
                                    "firstName": "John",
                                    "lastName": "Doe",
                                    "role": "USER",
                                    "active": true,
                                    "createTime": "2024-01-15T10:30:00",
                                    "updateTime": "2024-01-15T10:30:00"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Username or email already in use",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Conflict Error",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 409,
                                    "error": "Conflict",
                                    "message": "Username already in use"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Invalid input data",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Validation Error",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Email format is invalid"
                                }
                                """
                        )
                    }
                )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(
            @Parameter(description = "User registration details") @RequestBody UserRegistrationDTO registrationDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(registrationDTO));
    }

    @Operation(
        summary = "Authenticate user", 
        description = """
            Authenticates a user with Keycloak and returns JWT tokens.
            
            ## Process:
            1. Validates username and password against Keycloak
            2. Returns access token and refresh token
            3. Access token should be used for subsequent API calls
            Refresh token can be used to get new access tokens
            
            ## Token Usage:
            - **Access Token**: Include in Authorization header for API calls
            - **Refresh Token**: Use for getting new access tokens when expired
            - **Expires In**: Token expiration time in seconds
            
            ## Security:
            - Password is validated against Keycloak, not stored locally
            - No password data is returned in responses
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Login credentials",
            required = true,
            content = @Content(
                schema = @Schema(implementation = AuthRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Login Request",
                        summary = "User login",
                        description = "Authenticate with username and password",
                        value = """
                            {
                                "username": "john.doe",
                                "password": "SecurePassword123!"
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Authentication successful",
                content = @Content(
                    schema = @Schema(implementation = AuthResponse.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "access_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                                    "refresh_token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
                                    "expires_in": 300
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Invalid username or password",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Authentication Error",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Invalid credentials"
                                }
                                """
                        )
                    }
                )
            )
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<AuthResponse>> login(
            @Parameter(description = "Login credentials") @RequestBody AuthRequest request) {
        return userService.authenticate(request)
            .map(ResponseEntity::ok);
    }

    @Operation(
            summary = "Reset user password",
            description = """
            Resets a user's password directly in Keycloak.
            
            ## Process:
            1. Validates the provided password meets security requirements
            2. Looks up the user by username in the local database
            3. Updates the password in Keycloak
            
            ## Requirements:
            - Username must exist in the system
            - Password must meet the password policy (uppercase, lowercase, digit, special character)
            - Optional temporary flag will force the user to change password on next login
            
            ## Security:
            - Intended for password reset flows where the user has proven their identity (e.g. OTP/email verification)
            - Consider additional checks (token validation) in calling services or future iterations
            """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Password reset successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid request payload",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "Invalid Request",
                                            value = """
                            {
                                "timestamp": "2024-01-15T10:30:00",
                                "status": 400,
                                "error": "Bad Request",
                                "message": "Username is required"
                            }
                            """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "User Not Found",
                                            value = """
                            {
                                "timestamp": "2024-01-15T10:30:00",
                                "status": 404,
                                "error": "Not Found",
                                "message": "User with username unknown.user not found"
                            }
                            """
                                    )
                            }
                    )
            )
    })
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(
            @Parameter(description = "Reset password request") @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.noContent().build();
    }
    @Operation(
        summary = "Logout user", 
        description = """
            Invalidates the user's refresh token at Keycloak.
            
            ## Process:
            1. Takes the refresh token from the request
            2. Sends revocation request to Keycloak
            3. Token becomes invalid and cannot be used again
            
            ## Security:
            - Only the refresh token is invalidated
            - Access tokens will still work until they expire
            - For complete logout, client should also clear stored tokens
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Logout request with refresh token",
            required = true,
            content = @Content(
                schema = @Schema(implementation = LogoutRequest.class),
                examples = {
                    @ExampleObject(
                        name = "Logout Request",
                        summary = "User logout",
                        description = "Logout with refresh token",
                        value = """
                            {
                                "refreshToken": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9..."
                            }
                            """
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Logout successful",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = "No content returned"
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "Invalid token, token not provided, or token already invalidated",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Bad Request",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 400,
                                    "error": "Bad Request",
                                    "message": "Refresh token is required"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "Internal server error",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Server Error",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 500,
                                    "error": "Internal Server Error",
                                    "message": "Failed to revoke token"
                                }
                                """
                        )
                    }
                )
            )
    })
    @PostMapping("/logout")
    public Mono<ResponseEntity<Void>> logout(@RequestBody LogoutRequest req) {
        if (req.getRefreshToken() == null || req.getRefreshToken().isBlank()) {
            return Mono.just(ResponseEntity.badRequest().build());
        }
        return userService.logout(req.getRefreshToken())
            .thenReturn(ResponseEntity.ok().build());
    }
}
