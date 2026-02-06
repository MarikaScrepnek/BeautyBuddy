package com.beautybuddy.review;

import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.report.ReviewReport;
import com.beautybuddy.report.ReviewReportDTO;
import com.beautybuddy.report.ReviewReportRepository;
import com.beautybuddy.user.UserRepository;

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

    public ReviewService(ReviewRepository reviewRepository, ReviewReportRepository reviewReportRepository, UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
    }

    @Transactional
    public void addReview(String email, ReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(review.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = productShadeRepository.findByProductAndShadeName(product, review.shadeName())
            .orElseThrow(() -> new RuntimeException("Shade not found"));
        BigDecimal rating = review.rating();
        String reviewText = review.reviewText();
        List<ReviewImage> reviewImages = review.imageLinks() == null ? List.of() : review.imageLinks().stream()
            .map(link -> {
                ReviewImage img = new ReviewImage();
                img.setImageLink(link);
                img.setUploadedAt(LocalDateTime.now());
                return img;
            })
            .toList();
                    
        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setProductShade(shade);
        newReview.setRating(rating);
        newReview.setReviewText(reviewText);
        for (ReviewImage img : reviewImages) {
            img.setReview(newReview);
        }
        newReview.setReviewImages(reviewImages);

        reviewRepository.save(newReview);
    }

    @Transactional
    public void removeReview(String email, ReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(review.id())
            .orElseThrow(() -> new RuntimeException("Review not found"));
        if (existingReview.getUser().getUserId() != user.getUserId()) {
            throw new RuntimeException("User not authorized to delete this review");
        } else {
            reviewRepository.delete(existingReview);
        }
    }

    @Transactional
    public void reportReview(String email, ReviewReportDTO report) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review existingReview = reviewRepository.findById(report.reviewId())
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        ReviewReport newReport = new ReviewReport();
        newReport.setUser(user);
        newReport.setReview(existingReview);
        if (report.reason() != null && !report.reason().isEmpty()) {
            newReport.setReason(report.reason());
        }

        reviewReportRepository.save(newReport);
    }

    public BigDecimal getAverageRatingForProduct(int productId) {
        BigDecimal averageRating = reviewRepository.findAverageRatingByProductId(productId);
        return averageRating != null ? averageRating : BigDecimal.ZERO;
    }

    public Page<ReviewDTO> getReviewsForProduct(int productId, int page, int size) {
        Page<Review> reviewPage =
            reviewRepository.findByProduct_ProductIdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(
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

            return new ReviewDTO(
                review.getReviewId(),
                review.getProduct().getProductId(),
                shadeName,
                review.getRating(),
                review.getReviewText(),
                imageLinks
            );
        });
    }
}
