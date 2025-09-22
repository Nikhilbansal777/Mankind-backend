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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/users")
@Tag(name = "🔧 User Management (Admin)", description = "Admin API endpoints for managing all users and their addresses. Requires ADMIN role.")
@SecurityRequirement(name = "bearerAuth")
public class UserController {

    private final UserService userService;
    private final AddressService addressService;
    private final UserContextService userContextService;

    public UserController(UserService userService, AddressService addressService, UserContextService userContextService) {
        this.userService = userService;
        this.addressService = addressService;
        this.userContextService = userContextService;
    }

    @Operation(
        summary = "Get a user by ID (Admin only)", 
        description = """
            Returns a user based on the ID provided. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can access any user's data
            
            ## Returns:
            - Complete user information including ID, username, email
            - Personal details (first name, last name)
            - Role and active status
            - Profile picture URL
            - Creation and update timestamps
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "User found",
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
                description = "Unauthorized - Invalid or missing JWT token"
            ),
            @ApiResponse(
                responseCode = "403", 
                description = "Forbidden - Admin access required",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Forbidden",
                            value = """
                                {
                                    "timestamp": "2024-01-15T10:30:00",
                                    "status": 403,
                                    "error": "Forbidden",
                                    "message": "Admin access required"
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
                                    "message": "User with ID 999 not found"
                                }
                                """
                        )
                    }
                )
            )
    })
    @GetMapping("/{id}")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<UserDTO> getUser(
            @Parameter(description = "ID of the user to retrieve") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Operation(
        summary = "Get all users (Admin only)", 
        description = """
            Returns a list of all users in the system. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can access all users' data
            
            ## Returns:
            - List of all users with their basic information
            - Includes active and inactive users
            - Sorted by creation time (newest first)
            
            ## Pagination:
            - Currently returns all users
            - Consider implementing pagination for large datasets
            """)
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "200", 
                description = "List of users retrieved successfully",
                content = @Content(
                    examples = {
                        @ExampleObject(
                            name = "Success Response",
                            value = """
                                [
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
                                    },
                                    {
                                        "id": 2,
                                        "username": "admin.user",
                                        "email": "admin@example.com",
                                        "firstName": "Admin",
                                        "lastName": "User",
                                        "role": "ADMIN",
                                        "active": true,
                                        "createTime": "2024-01-14T09:00:00",
                                        "updateTime": "2024-01-14T09:00:00"
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            )
    })
    @GetMapping
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<Page<UserDTO>> getAllUsers(Pageable pageable) {
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(
        summary = "Update a user (Admin only)", 
        description = """
            Updates a user's information based on the ID provided. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can update any user's data
            
            ## Updateable Fields:
            - First name
            - Last name
            - Email (must be unique)
            - Profile picture URL
            
            ## Validation:
            - Email format validation
            - Email uniqueness check
            - Required field validation
            
            ## Note:
            - Admin can update any user's profile
            - Email changes are validated for uniqueness across all users
            """,
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Updated user information",
            required = true,
            content = @Content(
                schema = @Schema(implementation = UpdateUserDTO.class),
                examples = {
                    @ExampleObject(
                        name = "Update User",
                        summary = "Update user information",
                        description = "Update user profile as admin",
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
                description = "User updated successfully",
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
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
    @PutMapping("/{id}")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<UpdateUserDTO> updateUser(
            @Parameter(description = "ID of the user to update") @PathVariable Long id,
            @Parameter(description = "Updated user information") @RequestBody UpdateUserDTO updateUserDTO) {
        return ResponseEntity.ok(userService.updateUser(id, updateUserDTO));
    }

    // Address endpoints (Admin only)
    @Operation(
        summary = "Get all addresses for a user (Admin only)", 
        description = """
            Returns a list of all addresses associated with the specified user. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can access any user's addresses
            
            ## Address Types:
            - **billing**: Address for billing purposes
            - **shipping**: Address for shipping/delivery
            
            ## Default Addresses:
            - Users can have one default address per type
            - Default addresses are marked with `isDefault: true`
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found"
            )
    })
    @GetMapping("/{userId}/addresses")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<List<AddressDTO>> getUserAddresses(
            @Parameter(description = "ID of the user whose addresses to retrieve") @PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddressesByUserId(userId));
    }

    @Operation(
        summary = "Get a specific address (Admin only)", 
        description = """
            Returns a specific address for the specified user. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can access any user's address
            
            ## Validation:
            - Address ID must exist
            - Address must belong to the specified user
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user"
            )
    })
    @GetMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<AddressDTO> getUserAddress(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the address to retrieve") @PathVariable Long addressId) {
        AddressDTO addressDTO = addressService.getAddressById(addressId);
        // Verify that the address belongs to the specified user
        if (!addressDTO.getUserId().equals(userId)) {
            throw new AddressNotFoundException("Address with ID " + addressId + " not found for user with ID " + userId);
        }
        return ResponseEntity.ok(addressDTO);
    }

    @Operation(
        summary = "Create a new address for a user (Admin only)", 
        description = """
            Creates a new address for the specified user. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can create addresses for any user
            
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
                        description = "Create a new shipping address for user",
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "User not found"
            )
    })
    @PostMapping("/{userId}/addresses")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<AddressDTO> createAddress(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "Address details") @RequestBody CreateAddressDTO createAddressDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(addressService.createAddress(userId, createAddressDTO));
    }

    @Operation(
        summary = "Update an address for a user (Admin only)", 
        description = """
            Updates an existing address for the specified user. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can update any user's address
            
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
            
            ## Validation:
            - Address ID must exist
            - Address must belong to the specified user
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
                        description = "Update an existing address for user",
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user"
            )
    })
    @PutMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<AddressDTO> updateAddress(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the address to update") @PathVariable Long addressId,
            @Parameter(description = "Updated address details") @RequestBody UpdateAddressDTO updateAddressDTO) {
        // First check if the address exists and belongs to the user
        AddressDTO existingAddress = addressService.getAddressById(addressId);
        if (!existingAddress.getUserId().equals(userId)) {
            throw new AddressNotFoundException("Address with ID " + addressId + " not found for user with ID " + userId);
        }

        AddressDTO updatedAddress = addressService.updateAddress(addressId, updateAddressDTO);
        return ResponseEntity.ok(updatedAddress);
    }

    @Operation(
        summary = "Delete an address for a user (Admin only)", 
        description = """
            Deletes an address for the specified user. This endpoint is restricted to users with ADMIN role.
            
            ## Security:
            - Requires valid JWT token
            - Requires ADMIN role
            - Can delete any user's address
            
            ## Validation:
            - Address ID must exist
            - Address must belong to the specified user
            
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
                responseCode = "403", 
                description = "Forbidden - Admin access required"
            ),
            @ApiResponse(
                responseCode = "404", 
                description = "Address not found or doesn't belong to the user"
            )
    })
    @DeleteMapping("/{userId}/addresses/{addressId}")
    @PreAuthorize("@userContextService.isAdmin()")
    public ResponseEntity<Void> deleteAddress(
            @Parameter(description = "ID of the user") @PathVariable Long userId,
            @Parameter(description = "ID of the address to delete") @PathVariable Long addressId) {
        // First check if the address exists and belongs to the user
        AddressDTO existingAddress = addressService.getAddressById(addressId);
        if (!existingAddress.getUserId().equals(userId)) {
            throw new AddressNotFoundException("Address with ID " + addressId + " not found for user with ID " + userId);
        }

        addressService.deleteAddress(addressId);
        return ResponseEntity.noContent().build();
    }

    /**
     * INTERNAL USE ONLY: Batch user lookup for service-to-service calls. This endpoint is authenticated for performance reasons.
     */
    @Operation(
        summary = "Get users by IDs (batch)",
        description = "Returns a list of users for the given list of user IDs. INTERNAL USE ONLY for service-to-service calls. This endpoint is authenticated for performance reasons.",
        parameters = {
            @Parameter(name = "ids", description = "Comma-separated list of user IDs", example = "1,2,3", required = true)
        },
        responses = {
            @ApiResponse(
                responseCode = "200",
                description = "List of users found",
                content = @Content(
                    schema = @Schema(implementation = UserDTO.class)
                )
            ),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid or missing JWT token"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Admin access required")
        }
    )
    @GetMapping("/batch")
    public ResponseEntity<List<UserDTO>> getUsersByIds(@RequestParam("ids") List<Long> userIds) {
        return ResponseEntity.ok(userService.getUsersByIds(userIds));
    }
}
