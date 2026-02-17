package com.beautybuddy.review;

import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.upvote.UpvoteService;
import com.beautybuddy.review.dto.DisplayReviewDTO;
import com.beautybuddy.review.dto.SubmitReviewDTO;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UpvoteService upvoteService;

    public ReviewController(ReviewService reviewService, UpvoteService upvoteService) {
        this.reviewService = reviewService;
        this.upvoteService = upvoteService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addReview(@RequestBody SubmitReviewDTO reviewDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.addReview(userDetails.getEmail(), reviewDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> removeReview(@PathVariable Long reviewId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.removeReview(userDetails.getEmail(), reviewId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/report")
    public ResponseEntity<Void> reportReview(@PathVariable Long reviewId, @RequestBody String reason, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.reportReview(userDetails.getEmail(), reviewId, reason);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{reviewId}/upvote")
    public ResponseEntity<Void> upvoteReview(@PathVariable Long reviewId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        upvoteService.upvote(userDetails.getEmail(), "review", reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/average-rating")
    public ResponseEntity<BigDecimal> getAverageRatingForProduct(@PathVariable Long productId) {
        BigDecimal averageRating = reviewService.getAverageRatingForProduct(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<DisplayReviewDTO>> getReviewsForProduct(
        @PathVariable Long productId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication
    ) {
        String email = null;
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            email = userDetails.getEmail();
        }
        Page<DisplayReviewDTO> reviews = reviewService.getReviewsForProduct(productId, page, size, email);
        return ResponseEntity.ok(reviews);
    }

}
