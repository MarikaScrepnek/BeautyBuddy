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
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final ProductShadeRepository productShadeRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, ProductRepository productRepository, ProductShadeRepository productShadeRepository) {
        this.reviewRepository = reviewRepository;
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

    public void removeReview(String userEmail, ReviewDTO review) {
        // Implementation for removing a review
    }

    public void reportReview(String userEmail, ReviewDTO review) {
        // Implementation for reporting a review
    }

    public void upvoteReview(String userEmail, ReviewDTO review) {
        // Implementation for upvoting a review
    }

    public List<ReviewDTO> getReviewsForProduct(int productId) {
        // Implementation for retrieving reviews for a product
        return null; // Replace with actual implementation
    }
}