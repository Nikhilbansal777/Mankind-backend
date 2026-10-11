package com.mankind.mankindmatrixuserservice.controller;

import com.mankind.api.user.dto.AddressDTO;
import com.mankind.api.user.dto.CreateAddressDTO;
import com.mankind.api.user.dto.UpdateAddressDTO;
import com.mankind.api.user.dto.UpdateUserDTO;
import com.mankind.api.user.dto.UserDTO;
import com.mankind.mankindmatrixuserservice.exception.AddressNotFoundException;
import com.mankind.mankindmatrixuserservice.exception.UserNotFoundException;
import com.mankind.mankindmatrixuserservice.service.AddressService;
import com.mankind.mankindmatrixuserservice.service.UserContextService;
import com.mankind.mankindmatrixuserservice.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users/me")
@Tag(name = "👤 User Profile", description = "Self-service API endpoints for users to manage their own profile and addresses. Requires authentication.")
@SecurityRequirement(name = "bearerAuth")
public class UserProfileController {

    private final UserService userService;
    private final AddressService addressService;
    private final UserContextService userContextService;

    public UserProfileController(UserService userService, AddressService addressService, UserContextService userContextService) {
        this.userService = userService;
        this.addressService = addressService;
        this.userContextService = userContextService;
    }

    @Operation(
        summary = "Get current user profile", 
        description = """
            Returns the current authenticated user's profile information.
            
            ## Security:
            - Requires valid JWT token
            - Users can only access their own profile
            - Token must be included in Authorization header
            
            ## Returns:
            - User ID, username, email
            - First name, last name
            - Role and active status
            - Profile picture URL
            - Creation and update timestamps
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "User profile retrieved successfully",
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
                                    "profilePictureUrl": "https://example.com/avatar.jpg",
                                    "createTime": "2024-01-15T10:30:00",
                                    "updateTime": "2024-01-15T10:30:00"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Unauthorized",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 401,
                                    "error": "Unauthorized",
                                    "message": "Invalid JWT token"
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
                            name = "Not Found",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 404,
                                    "error": "Not Found",
                                    "message": "Current user not found"
                                }
                                """
                        )
                    }
                )
            )
    })
    @GetMapping
    public ResponseEntity<UserDTO> getCurrentUserProfile() {
        return userContextService.getCurrentUserId()
                .map(userId -> ResponseEntity.ok(userService.getUserById(userId)))
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    @Operation(
        summary = "Update current user profile", 
        description = """
            Updates the current authenticated user's profile information.
            
            ## Security:
            - Requires valid JWT token
            - Users can only update their own profile
            - Email changes are validated for uniqueness
            
            ## Updateable Fields:
            - First name
            - Last name
            - Email (must be unique)
            - Profile picture URL
            
            ## Validation:
            - Email format validation
            - Email uniqueness check
            - Required field validation
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated user information",
            required = true,
            content = @Content(
                schema = @Schema(implementation = UpdateUserDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Update Profile",
                        summary = "Update user profile",
                        description = "Update user profile information",
                        value = """
                            {
                                "firstName": "John Updated",
                                "lastName": "Doe Updated",
                                "email": "john.updated@example.com",
                                "profilePictureUrl": "https://example.com/new-avatar.jpg"
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
                description = "User profile updated successfully",
                content = @Content(
                    schema = @Schema(implementation = UpdateUserDTO.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "firstName": "John Updated",
                                    "lastName": "Doe Updated",
                                    "email": "john.updated@example.com",
                                    "profilePictureUrl": "https://example.com/new-avatar.jpg"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found"
            ),
            @ApiResponse(
                responseCode = "409", 
                description = "Conflict - Email already in use",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Conflict Error",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 409,
                                    "error": "Conflict",
                                    "message": "Email already in use"
                                }
                                """
                        )
                    }
                )
            )
    })
    @PutMapping
    public ResponseEntity<UpdateUserDTO> updateCurrentUserProfile(
            @Parameter(description = "Updated user information") @RequestBody UpdateUserDTO updateUserDTO) {
        return userContextService.getCurrentUserId()
                .map(userId -> ResponseEntity.ok(userService.updateUser(userId, updateUserDTO)))
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    // Address endpoints for current user
    @Operation(
        summary = "Get current user's addresses", 
        description = """
            Returns all addresses associated with the current authenticated user.
            
            ## Address Types:
            - **billing**: Address for billing purposes
            - **shipping**: Address for shipping/delivery
            
            ## Default Addresses:
            - Users can have one default address per type
            - Default addresses are marked with `isDefault: true`
            
            ## Security:
            - Requires valid JWT token
            - Users can only access their own addresses
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Addresses retrieved successfully",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                [
                                    {
                                        "id": 1,
                                        "userId": 1,
                                        "addressType": "shipping",
                                        "isDefault": true,
                                        "streetAddress": "123 Main St",
                                        "city": "New York",
                                        "state": "NY",
                                        "postalCode": "10001",
                                        "country": "USA",
                                        "createdAt": "2024-01-15T10:30:00",
                                        "updatedAt": "2024-01-15T10:30:00"
                                    },
                                    {
                                        "id": 2,
                                        "userId": 1,
                                        "addressType": "billing",
                                        "isDefault": true,
                                        "streetAddress": "456 Business Ave",
                                        "city": "New York",
                                        "state": "NY",
                                        "postalCode": "10002",
                                        "country": "USA",
                                        "createdAt": "2024-01-15T10:30:00",
                                        "updatedAt": "2024-01-15T10:30:00"
                                    }
                                ]
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found"
            )
    })
    @GetMapping("/addresses")
    public ResponseEntity<List<AddressDTO>> getCurrentUserAddresses() {
        return userContextService.getCurrentUserId()
                .map(userId -> ResponseEntity.ok(addressService.getAddressesByUserId(userId)))
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    @Operation(
        summary = "Get a specific address for current user", 
        description = """
            Returns a specific address for the current authenticated user.
            
            ## Security:
            - Requires valid JWT token
            - Users can only access their own addresses
            - Address must belong to the current user
            
            ## Validation:
            - Address ID must exist
            - Address must belong to the current user
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "Address found",
                content = @Content(
                    schema = @Schema(implementation = AddressDTO.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "id": 1,
                                    "userId": 1,
                                    "addressType": "shipping",
                                    "isDefault": true,
                                    "streetAddress": "123 Main St",
                                    "city": "New York",
                                    "state": "NY",
                                    "postalCode": "10001",
                                    "country": "USA",
                                    "createdAt": "2024-01-15T10:30:00",
                                    "updatedAt": "2024-01-15T10:30:00"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Not Found",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 404,
                                    "error": "Not Found",
                                    "message": "Address with ID 999 not found for current user"
                                }
                                """
                        )
                    }
                )
            )
    })
    @GetMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> getCurrentUserAddress(
            @Parameter(description = "ID of the address to retrieve") @PathVariable Long addressId) {
        return userContextService.getCurrentUserId()
                .map(userId -> {
                    AddressDTO addressDTO = addressService.getAddressById(addressId);
                    if (!addressDTO.getUserId().equals(userId)) {
                        throw new AddressNotFoundException("Address with ID " + addressId + " not found for current user");
                    }
                    return ResponseEntity.ok(addressDTO);
                })
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    @Operation(
        summary = "Create a new address for current user", 
        description = """
            Creates a new address for the current authenticated user.
            
            ## Address Types:
            - **billing**: Address for billing purposes
            - **shipping**: Address for shipping/delivery
            
            ## Default Address Logic:
            - If `isDefault` is set to `true`, any existing default address of the same type will be set to `false`
            - Users can have one default address per type
            
            ## Required Fields:
            - `addressType`: billing or shipping
            - `streetAddress`: Street address
            - `city`: City name
            - `state`: State/province
            - `postalCode`: Postal/ZIP code
            - `country`: Country name
            
            ## Optional Fields:
            - `isDefault`: Whether this is the default address for the type (default: false)
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Address details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = CreateAddressDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Shipping Address",
                        summary = "Create shipping address",
                        description = "Create a new shipping address",
                        value = """
                            {
                                "addressType": "shipping",
                                "isDefault": true,
                                "streetAddress": "123 Main St",
                                "city": "New York",
                                "state": "NY",
                                "postalCode": "10001",
                                "country": "USA"
                            }
                            """
                    ),
                    @ExampleObject(
                        name = "Billing Address",
                        summary = "Create billing address",
                        description = "Create a new billing address",
                        value = """
                            {
                                "addressType": "billing",
                                "isDefault": false,
                                "streetAddress": "456 Business Ave",
                                "city": "New York",
                                "state": "NY",
                                "postalCode": "10002",
                                "country": "USA"
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
                description = "Address created successfully",
                content = @Content(
                    schema = @Schema(implementation = AddressDTO.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "id": 3,
                                    "userId": 1,
                                    "addressType": "shipping",
                                    "isDefault": true,
                                    "streetAddress": "123 Main St",
                                    "city": "New York",
                                    "state": "NY",
                                    "postalCode": "10001",
                                    "country": "USA",
                                    "createdAt": "2024-01-15T10:30:00",
                                    "updatedAt": "2024-01-15T10:30:00"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found"
            )
    })
    @PostMapping("/addresses")
    public ResponseEntity<AddressDTO> createAddressForCurrentUser(
            @Parameter(description = "Address details") @RequestBody CreateAddressDTO createAddressDTO) {
        return userContextService.getCurrentUserId()
                .map(userId -> {
                    AddressDTO createdAddress = addressService.createAddress(userId, createAddressDTO);
                    return ResponseEntity.status(HttpStatus.CREATED).body(createdAddress);
                })
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    @Operation(
        summary = "Update an address for current user", 
        description = """
            Updates an existing address for the current authenticated user.
            
            ## Security:
            - Requires valid JWT token
            - Users can only update their own addresses
            - Address must belong to the current user
            
            ## Updateable Fields:
            - `addressType`: billing or shipping
            - `isDefault`: Whether this is the default address
            - `streetAddress`: Street address
            - `city`: City name
            - `state`: State/province
            - `postalCode`: Postal/ZIP code
            - `country`: Country name
            
            ## Default Address Logic:
            - If `isDefault` is set to `true`, any existing default address of the same type will be set to `false`
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated address details",
            required = true,
            content = @Content(
                schema = @Schema(implementation = UpdateAddressDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Update Address",
                        summary = "Update address",
                        description = "Update an existing address",
                        value = """
                            {
                                "streetAddress": "789 Updated St",
                                "city": "Los Angeles",
                                "state": "CA",
                                "postalCode": "90210",
                                "country": "USA",
                                "isDefault": true
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
                description = "Address updated successfully",
                content = @Content(
                    schema = @Schema(implementation = AddressDTO.class),
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                {
                                    "id": 1,
                                    "userId": 1,
                                    "addressType": "shipping",
                                    "isDefault": true,
                                    "streetAddress": "789 Updated St",
                                    "city": "Los Angeles",
                                    "state": "CA",
                                    "postalCode": "90210",
                                    "country": "USA",
                                    "createdAt": "2024-01-15T10:30:00",
                                    "updatedAt": "2024-01-15T10:35:00"
                                }
                                """
                        )
                    }
                )
            ),
            @ApiResponse(
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user"
            )
    })
    @PutMapping("/addresses/{addressId}")
    public ResponseEntity<AddressDTO> updateAddressForCurrentUser(
            @Parameter(description = "ID of the address to update") @PathVariable Long addressId,
            @Parameter(description = "Updated address details") @RequestBody UpdateAddressDTO updateAddressDTO) {
        return userContextService.getCurrentUserId()
                .map(userId -> {
                    // Verify that the address belongs to the current user
                    AddressDTO existingAddress = addressService.getAddressById(addressId);
                    if (!existingAddress.getUserId().equals(userId)) {
                        throw new AddressNotFoundException("Address with ID " + addressId + " not found for current user");
                    }
                    AddressDTO updatedAddress = addressService.updateAddress(addressId, updateAddressDTO);
                    return ResponseEntity.ok(updatedAddress);
                })
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }

    @Operation(
        summary = "Delete an address for current user", 
        description = """
            Deletes an address for the current authenticated user.
            
            ## Security:
            - Requires valid JWT token
            - Users can only delete their own addresses
            - Address must belong to the current user
            
            ## Validation:
            - Address ID must exist
            - Address must belong to the current user
            
            ## Note:
            - This operation is irreversible
            - If the deleted address was the default, no new default will be automatically set
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "204", 
                description = "Address deleted successfully",
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
                responseCode = "401", 
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Not Found",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 404,
                                    "error": "Not Found",
                                    "message": "Address with ID 999 not found for current user"
                                }
                                """
                        )
                    }
                )
            )
    })
    @DeleteMapping("/addresses/{addressId}")
    public ResponseEntity<Void> deleteAddressForCurrentUser(
            @Parameter(description = "ID of the address to delete") @PathVariable Long addressId) {
        return userContextService.getCurrentUserId()
                .map(userId -> {
                    // Verify that the address belongs to the current user
                    AddressDTO existingAddress = addressService.getAddressById(addressId);
                    if (!existingAddress.getUserId().equals(userId)) {
                        throw new AddressNotFoundException("Address with ID " + addressId + " not found for current user");
                    }
                    addressService.deleteAddress(addressId);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElseThrow(() -> new UserNotFoundException("Current user not found"));
    }
} 