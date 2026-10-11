package com.mankind.matrix_product_service.service;

import com.mankind.api.product.dto.review.CreateReviewDTO;
import com.mankind.api.product.dto.review.ReviewDTO;
import com.mankind.api.user.dto.UserDTO;
import com.mankind.api.product.dto.review.ReviewReturnDTO;
import com.mankind.matrix_product_service.model.Product;
import com.mankind.matrix_product_service.model.Review;
import com.mankind.matrix_product_service.repository.ProductRepository;
import com.mankind.matrix_product_service.repository.ReviewRepository;
import com.mankind.matrix_product_service.client.UserClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;
import feign.FeignException;
import com.mankind.matrix_product_service.exception.ResourceNotFoundException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CurrentUserService currentUserService;

    @Autowired
    private UserClient userClient;

    @Transactional
    public ReviewDTO createReview(CreateReviewDTO createReviewDTO) {
        Product product = productRepository.findById(createReviewDTO.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = new Review();
        review.setUserId(currentUserService.getCurrentUserId());
        review.setProduct(product);
        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());

        Review savedReview = reviewRepository.save(review);
        updateProductAverageRating(product.getId());
        return convertToDTO(savedReview);
    }

    public Page<ReviewReturnDTO> getReviewsReturnByProductId(Long productId, Pageable pageable) {
        Page<Review> reviews = reviewRepository.findByProductId(productId, pageable);
        List<Long> userIds = reviews.stream().map(Review::getUserId).distinct().toList();
        boolean userInfoAvailableTmp = true;
        List<UserDTO> users = List.of();
        try {
            users = userClient.getUsersByIds(userIds);
        } catch (FeignException.Unauthorized | FeignException.Forbidden | org.springframework.web.server.ResponseStatusException e) {
            userInfoAvailableTmp = false;
        }
        final boolean userInfoAvailable = userInfoAvailableTmp;
        final Map<Long, UserDTO> userMap = users.stream().collect(Collectors.toMap(UserDTO::getId, u -> u));
        return reviews.map(review -> {
            String username = userInfoAvailable && userMap.containsKey(review.getUserId()) ? userMap.get(review.getUserId()).getUsername() : null;
            Long userId = userInfoAvailable && userMap.containsKey(review.getUserId()) ? review.getUserId() : null;
            return new ReviewReturnDTO(
                review.getId(),
                userId,
                username,
                review.getProduct().getId(),
                review.getRating(),
                review.getComment(),
                review.getCreatedAt(),
                review.getUpdatedAt()
            );
        });
    }

    public Page<ReviewReturnDTO> getReviewsReturnByUserId(Long userId, Pageable pageable) {
        List<UserDTO> users;
        try {
            users = userClient.getUsersByIds(List.of(userId));
        } catch (FeignException.Forbidden e) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not authorized to access user with id: " + userId);
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
        if (users == null || users.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        UserDTO user = users.get(0);
        String username = user != null ? user.getUsername() : null;
        Page<Review> reviews = reviewRepository.findByUserId(userId, pageable);
        return reviews.map(review -> new ReviewReturnDTO(
            review.getId(),
            review.getUserId(),
            username,
            review.getProduct().getId(),
            review.getRating(),
            review.getComment(),
            review.getCreatedAt(),
            review.getUpdatedAt()
        ));
    }

    @Transactional
    public ReviewDTO updateReview(Long reviewId, CreateReviewDTO createReviewDTO) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Only allow the current user to update their own review
        Long currentUserId = currentUserService.getCurrentUserId();
        if (!review.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You can only update your own reviews");
        }

        review.setRating(createReviewDTO.getRating());
        review.setComment(createReviewDTO.getComment());

        Review updatedReview = reviewRepository.save(review);
        updateProductAverageRating(review.getProduct().getId());
        return convertToDTO(updatedReview);
    }

    @Transactional
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        Long currentUserId = currentUserService.getCurrentUserId();
        if (!review.getUserId().equals(currentUserId)) {
            throw new RuntimeException("You can only delete your own reviews");
        }
        Long productId = review.getProduct().getId();
        reviewRepository.deleteById(reviewId);
        updateProductAverageRating(productId);
    }

    private ReviewDTO convertToDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setUserId(review.getUserId());
        dto.setProductId(review.getProduct().getId());
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        return dto;
    }

    private void updateProductAverageRating(Long productId) {
        List<Review> reviews = reviewRepository.findTop100ByProductIdOrderByCreatedAtDesc(productId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        if (reviews.isEmpty()) {
            product.setAverageRating(null);
        } else {
            double avg = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
            product.setAverageRating(avg);
        }
        productRepository.save(product);
    }
} 