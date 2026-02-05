package com.beautybuddy.review;

import com.beautybuddy.product.ProductRepository;
import com.beautybuddy.product.ProductShadeRepository;
import com.beautybuddy.user.UserRepository;

import com.beautybuddy.user.User;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;

import java.math.BigDecimal;
import java.util.List;

public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final ReviewUpvoteRepository reviewUpvoteRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;

    public ReviewService(ReviewRepository reviewRepository, ReviewReportRepository reviewReportRepository, ReviewUpvoteRepository reviewUpvoteRepository, UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository) {
        this.reviewRepository = reviewRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.reviewUpvoteRepository = reviewUpvoteRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.productShadeRepository = productShadeRepository;
    }

    public void addReview(String email, ReviewDTO review) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(review.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));
        ProductShade shade = productShadeRepository.findByProductAndShadeName(product, review.shadeName())
            .orElseThrow(() -> new RuntimeException("Shade not found"));
        BigDecimal rating = review.rating();
        String reviewText = review.reviewText();
        List<ReviewImage> reviewImages = review.images().stream()
            .map(imgDto -> {
                ReviewImage img = new ReviewImage();
                img.setImageLink(imgDto.imageLink());
                return img;
            })
            .toList();
        
        Review newReview = new Review();
        newReview.setUser(user);
        newReview.setProduct(product);
        newReview.setProductShade(shade);
        newReview.setRating(rating);
        newReview.setReviewText(reviewText);
        newReview.setReviewImages(reviewImages);

        reviewRepository.save(newReview);
    }

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

    public void upvoteReview(String email, ReviewUpvoteDTO upvote) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review review = reviewRepository.findById(upvote.reviewId())
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        ReviewUpvote newUpvote = new ReviewUpvote();
        newUpvote.setUserId(user.getUserId());
        newUpvote.setReviewId(review.getReviewId());

        reviewUpvoteRepository.save(newUpvote);
    }

    public List<ReviewDTO> getReviewsForProduct(int productId) {
        // Implementation for retrieving reviews for a product
        return null; // Replace with actual implementation
    }
}
