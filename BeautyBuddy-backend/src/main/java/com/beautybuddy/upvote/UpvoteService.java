package com.beautybuddy.upvote;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.review.Review;
import com.beautybuddy.upvote.entity.ReviewUpvote;
import com.beautybuddy.user.User;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.upvote.repo.ReviewUpvoteRepository;

@Service
public class UpvoteService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewUpvoteRepository reviewUpvoteRepository;

    public UpvoteService(ReviewRepository reviewRepository, UserRepository userRepository, ReviewUpvoteRepository reviewUpvoteRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewUpvoteRepository = reviewUpvoteRepository;
    }

    @Transactional
    public void upvote(String email, UpvoteRequestDTO upvote) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Review review = reviewRepository.findById(upvote.targetId())
            .orElseThrow(() -> new RuntimeException("Review not found"));
        
        ReviewUpvote newUpvote = new ReviewUpvote();
        newUpvote.setUser(user);
        newUpvote.setReview(review);

        reviewUpvoteRepository.save(newUpvote);
    }
}
