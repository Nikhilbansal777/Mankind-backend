package com.mankind.matrix_wishlistservice.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.mankind.matrix_wishlistservice.dto.SuccessResponse;
import com.mankind.matrix_wishlistservice.exception.ErrorResponse;
import com.mankind.matrix_wishlistservice.model.WishlistItem;
import com.mankind.matrix_wishlistservice.service.WishlistService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/wishlist")
@Tag(name = "Wishlist Management", description = "API endpoints for managing user wishlists")
@SecurityRequirement(name = "bearerAuth")
public class WishlistController {

    private final WishlistService service;

    public WishlistController(WishlistService service) {
        this.service = service;
    }

    @Operation(
        summary = "Add item to wishlist",
        description = "Adds a product to a user's wishlist. If the product is already in the wishlist, returns a 400 error.",
        requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Product details to add to wishlist",
            required = true,
            content = @Content(
                mediaType = "application/x-www-form-urlencoded",
                schema = @Schema(implementation = WishlistItem.class),
                examples = {
                    @ExampleObject(
                        name = "Basic Add",
                        value = "userId=1&productId=100",
                        description = "Basic request with just user and product IDs"
                    ),
                    @ExampleObject(
                        name = "Full Details",
                        value = "userId=1&productId=100&name=iPhone 13&brand=Apple&price=999.99&imageUrl=https://example.com/iphone13.jpg",
                        description = "Request with all product details"
                    )
                }
            )
        )
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item added successfully",
            content = @Content(
                schema = @Schema(implementation = WishlistItem.class),
                examples = {
                    @ExampleObject(
                        name = "Success Response",
                        value = """
                            {
                                "id": 1,
                                "userId": 1,
                                "productId": 100,
                                "name": "iPhone 13",
                                "brand": "Apple",
                                "price": 999.99,
                                "imageUrl": "https://example.com/iphone13.jpg"
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "Item already in wishlist",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Duplicate Item Error",
                        value = """
                            {
                                "message": "Item already in wishlist",
                                "status": 400
                            }
                            """
                    )
                }
            )
        )
    })
    @PostMapping
    public WishlistItem add(
            @Parameter(description = "ID of the user", example = "1", required = true) 
            @RequestParam Long userId,
            @Parameter(description = "ID of the product to add", example = "100", required = true) 
            @RequestParam Long productId,
            @Parameter(description = "Name of the product", example = "iPhone 13") 
            @RequestParam(required = false) String name,
            @Parameter(description = "Brand of the product", example = "Apple") 
            @RequestParam(required = false) String brand,
            @Parameter(description = "Price of the product", example = "999.99") 
            @RequestParam(required = false) java.math.BigDecimal price,
            @Parameter(description = "Image URL of the product", example = "https://example.com/iphone13.jpg") 
            @RequestParam(required = false) String imageUrl) {
        return service.addItem(userId, productId, name, brand, price, imageUrl);
    }

    @Operation(
        summary = "Get user's wishlist",
        description = "Returns all items in a user's wishlist. If the user has no items or doesn't exist, returns a 404 error."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Wishlist retrieved successfully",
            content = @Content(
                schema = @Schema(implementation = WishlistItem.class),
                examples = {
                    @ExampleObject(
                        name = "Success Response",
                        value = """
                            [
                                {
                                    "id": 1,
                                    "userId": 1,
                                    "productId": 100,
                                    "name": "iPhone 13",
                                    "brand": "Apple",
                                    "price": 999.99,
                                    "imageUrl": "https://example.com/iphone13.jpg"
                                },
                                {
                                    "id": 2,
                                    "userId": 1,
                                    "productId": 101,
                                    "name": "MacBook Pro",
                                    "brand": "Apple",
                                    "price": 1299.99,
                                    "imageUrl": "https://example.com/macbook.jpg"
                                }
                            ]
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found or wishlist is empty",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Not Found Error",
                        value = """
                            {
                                "message": "User not found or no wishlist items available for user 1",
                                "status": 404
                            }
                            """
                    )
                }
            )
        )
    })
    @GetMapping("/{userId}")
    public List<WishlistItem> get(
            @Parameter(description = "ID of the user", example = "1", required = true) 
            @PathVariable Long userId) {
        return service.getUserWishlist(userId);
    }

    @Operation(
        summary = "Remove item from wishlist",
        description = "Removes a product from a user's wishlist. If the user or product is not found, returns a 404 error."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Item removed successfully",
            content = @Content(
                schema = @Schema(implementation = SuccessResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Success Response",
                        value = """
                            {
                                "message": "Item successfully removed from wishlist",
                                "status": 200
                            }
                            """
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found, no wishlist items available, or product not in wishlist",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Not Found Error",
                        value = """
                            {
                                "message": "Product 100 not found in wishlist for user 1",
                                "status": 404
                            }
                            """
                    )
                }
            )
        )
    })
    @DeleteMapping
    public SuccessResponse delete(
            @Parameter(description = "ID of the user", example = "1", required = true) 
            @RequestParam Long userId,
            @Parameter(description = "ID of the product to remove", example = "100", required = true) 
            @RequestParam Long productId) {
        service.removeItem(userId, productId);
        return new SuccessResponse("Item successfully removed from wishlist", 200);
    }

    @Operation(
        summary = "Check if item is in wishlist",
        description = "Checks if a product is in a user's wishlist. Returns true if the product is found, false otherwise."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Check completed successfully",
            content = @Content(
                schema = @Schema(type = "boolean"),
                examples = {
                    @ExampleObject(
                        name = "Item Found",
                        value = "true",
                        description = "Product is in the wishlist"
                    ),
                    @ExampleObject(
                        name = "Item Not Found",
                        value = "false",
                        description = "Product is not in the wishlist"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "User not found, no wishlist items available, or product not in wishlist",
            content = @Content(
                schema = @Schema(implementation = ErrorResponse.class),
                examples = {
                    @ExampleObject(
                        name = "Not Found Error",
                        value = """
                            {
                                "message": "Product 100 not found in wishlist for user 1",
                                "status": 404
                            }
                            """
                    )
                }
            )
        )
    })
    @GetMapping("/check")
    public boolean check(
            @Parameter(description = "ID of the user", example = "1", required = true) 
            @RequestParam Long userId,
            @Parameter(description = "ID of the product to check", example = "100", required = true) 
            @RequestParam Long productId) {
        return service.isInWishlist(userId, productId);
    }
}
