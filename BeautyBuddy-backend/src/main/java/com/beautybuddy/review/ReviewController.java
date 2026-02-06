package com.beautybuddy.review;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;

import com.beautybuddy.report.ReviewDTO;
import com.beautybuddy.report.ReviewReportDTO;
import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.upvote.UpvoteRequestDTO;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping("/add")
    public ResponseEntity<Void> addReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.addReview(userDetails.getEmail(), reviewDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/remove")
    public ResponseEntity<Void> removeReview(@RequestBody ReviewDTO reviewDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.removeReview(userDetails.getEmail(), reviewDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/report")
    public ResponseEntity<Void> reportReview(@RequestBody ReviewReportDTO reviewReportDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.reportReview(userDetails.getEmail(), reviewReportDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/upvote")
    public ResponseEntity<Void> upvoteReview(@RequestBody UpvoteRequestDTO upvoteRequestDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reviewService.upvoteReview(userDetails.getEmail(), upvoteRequestDTO);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{productId}/average-rating")
    public ResponseEntity<BigDecimal> getAverageRatingForProduct(@PathVariable int productId) {
        BigDecimal averageRating = reviewService.getAverageRatingForProduct(productId);
        return ResponseEntity.ok(averageRating);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<Page<ReviewDTO>> getReviewsForProduct(
        @PathVariable int productId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        Page<ReviewDTO> reviews = reviewService.getReviewsForProduct(productId, page, size);
        return ResponseEntity.ok(reviews);
    }

}
