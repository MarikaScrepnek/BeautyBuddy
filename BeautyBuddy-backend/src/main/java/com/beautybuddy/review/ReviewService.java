package com.beautybuddy.review;

import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.report.entity.ReviewReport;
import com.beautybuddy.report.repo.ReviewReportRepository;
import com.beautybuddy.review.dto.DisplayReviewDTO;
import com.beautybuddy.review.dto.SubmitReviewDTO;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.review.entity.ReviewImage;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.upvote.repo.ReviewUpvoteRepository;
import com.beautybuddy.review.dto.EditReviewDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.user.User;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@Service
@Transactional(readOnly = true)
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;
    private final ReviewUpvoteRepository reviewUpvoteRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewReportRepository reviewReportRepository, UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository, ReviewUpvoteRepository reviewUpvoteRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
        this.reviewUpvoteRepository = reviewUpvoteRepository;
    }

    @Transactional
    public void addReview(String email, SubmitReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(review.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = null;
        if (review.shadeName() != null) {
            shade = productShadeRepository.findByProductAndShadeName(product, review.shadeName())
                .orElseThrow(() -> new RuntimeException("Shade not found"));
        }
        BigDecimal rating = review.rating();
        String reviewTitle = review.title();
        String reviewText = review.text();
        List<ReviewImage> reviewImages = review.imageLinks() == null ? List.of() : review.imageLinks().stream()
            .map(link -> {
                ReviewImage img = new ReviewImage();
                img.setImageLink(link);
                img.setCreatedAt(LocalDateTime.now());
                return img;
            })
            .toList();
                    
        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setProductShade(shade);
        newReview.setRating(rating);
        newReview.setTitle(reviewTitle);
        newReview.setText(reviewText);
        for (ReviewImage img : reviewImages) {
            img.setReview(newReview);
        }
        newReview.setReviewImages(reviewImages);

        reviewRepository.save(newReview);
    }

    @Transactional
    public void editReview(String email, Long reviewId, EditReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        if (existingReview.getUser().getId() != user.getId()) {
            throw new RuntimeException("User not authorized to edit this review");
        } else {
            ProductShade shade = null;
            if (review.shadeName() != null) {
                shade = productShadeRepository.findByProductAndShadeName(existingReview.getProduct(), review.shadeName())
                    .orElseThrow(() -> new RuntimeException("Shade not found"));
            }
            existingReview.setProductShade(shade);
            existingReview.setRating(review.rating());
            existingReview.setTitle(review.title());
            existingReview.setText(review.text());

            // Handle review images
            List<ReviewImage> newImages = review.imageLinks() == null ? List.of() : review.imageLinks().stream()
                .map(link -> {
                    ReviewImage img = new ReviewImage();
                    img.setImageLink(link);
                    img.setCreatedAt(LocalDateTime.now());
                    img.setReview(existingReview);
                    return img;
                })
                .toList();
            
            // Remove old images
            existingReview.getReviewImages().clear();
            // Add new images
            existingReview.getReviewImages().addAll(newImages);

            reviewRepository.save(existingReview);
        }
    }

    @Transactional
    public void removeReview(String email, Long reviewId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        if (existingReview.getUser().getId() != user.getId()) {
            throw new RuntimeException("User not authorized to delete this review");
        } else {
            existingReview.setDeletedAt(LocalDateTime.now());
            reviewRepository.save(existingReview);
        }
    }

    @Transactional
    public void reportReview(String email, Long reviewId, String reason) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(reviewId)
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        ReviewReport newReport = new ReviewReport();
        newReport.setUser(user);
        newReport.setReview(existingReview);
        if (reason != null && !reason.isEmpty()) {
            newReport.setReason(reason);
        }

        reviewReportRepository.save(newReport);
    }

    public BigDecimal getAverageRatingForProduct(Long productId) {
        BigDecimal averageRating = reviewRepository.findAverageRatingByProductId(productId);
        return averageRating != null ? averageRating : BigDecimal.ZERO;
    }

    public Page<DisplayReviewDTO> getReviewsForProduct(Long productId, int page, int size, String userEmail) {
        final User currentUser = userEmail != null
            ? userRepository.findByEmail(userEmail).orElse(null)
            : null;
        Page<Review> reviewPage =
            reviewRepository.findByProduct_IdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(
                productId,
                PageRequest.of(page, size)
            );

        return reviewPage.map(review -> {
            List<String> imageLinks = review.getReviewImages().stream()
                .map(img -> img.getImageLink())
                .toList();

            String shadeName = review.getProductShade() != null
                ? review.getProductShade().getShadeName()
                : null;

            boolean hasUpvoted = currentUser != null
                && reviewUpvoteRepository.findByUserAndReview(currentUser, review).isPresent();

            return new DisplayReviewDTO(
                review.getId(),
                review.getUser().getUsername(),
                review.getUser().getAvatarLink(),
                review.getRating(),
                review.getCreatedAt(),
                review.getProduct().getId(),
                shadeName,
                review.getTitle(),
                review.getText(),
                imageLinks,
                review.getUpvoteCount(),
                hasUpvoted
            );
        });
    }
}
