package com.mankind.matrix_product_service.controller;

import com.mankind.api.product.dto.review.CreateReviewDTO;
import com.mankind.api.product.dto.review.ReviewDTO;
import com.mankind.api.product.dto.review.ReviewReturnDTO;
import com.mankind.matrix_product_service.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@Tag(name = "Review Management", description = "APIs for managing product reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "Create a new review", description = "Creates a new review for a product.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review successfully created",
                    content = @Content(schema = @Schema(implementation = ReviewDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<ReviewDTO> createReview(
            @Parameter(description = "Review object to be created", required = true)
            @Valid @RequestBody CreateReviewDTO createReviewDTO) {
        return ResponseEntity.ok(reviewService.createReview(createReviewDTO));
    }

    @Operation(summary = "Get reviews by product ID", description = "Retrieves all reviews for a specific product, including user info (userId, username)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews",
                    content = @Content(schema = @Schema(implementation = ReviewReturnDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewReturnDTO>> getReviewsByProductId(
            @Parameter(description = "ID of the product", required = true)
            @PathVariable Long productId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsReturnByProductId(productId, pageable));
    }

    @Operation(summary = "Get reviews by user ID", description = "Retrieves all reviews by a specific user, including user info (userId, username)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved reviews",
                    content = @Content(schema = @Schema(implementation = ReviewReturnDTO.class))),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ReviewReturnDTO>> getReviewsByUserId(
            @Parameter(description = "ID of the user", required = true)
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(reviewService.getReviewsReturnByUserId(userId, pageable));
    }

    @Operation(summary = "Update review", description = "Updates an existing review.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review successfully updated",
                    content = @Content(schema = @Schema(implementation = ReviewDTO.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @Parameter(description = "ID of the review to update", required = true)
            @PathVariable Long reviewId,
            @Parameter(description = "Updated review object", required = true)
            @Valid @RequestBody CreateReviewDTO createReviewDTO) {
        return ResponseEntity.ok(reviewService.updateReview(reviewId, createReviewDTO));
    }

    @Operation(summary = "Delete review", description = "Deletes a review")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Review successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Review not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "ID of the review to delete", required = true)
            @PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }
} 