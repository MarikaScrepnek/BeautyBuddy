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
    public void upvote(String email, String targetType, Long targetId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (targetType.equals("review")) {
            Review review = reviewRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
            if (reviewUpvoteRepository.findByUserAndReview(user, review).isPresent()) {
                return;
            }
        
            ReviewUpvote newUpvote = new ReviewUpvote();
            newUpvote.setUser(user);
            newUpvote.setReview(review);

            reviewUpvoteRepository.save(newUpvote);
        }
        else if (targetType.equals("question")) {
            // Handle comment upvote logic here
        }
        else if (targetType.equals("answer")) {
            // Handle answer upvote logic here
        }
        else if (targetType.equals("discussion")) {
            // Handle discussion upvote logic here
        }
        else if (targetType.equals("discussion_comment")) {
            // Handle discussion comment upvote logic here
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }

    @Transactional
    public void removeUpvote(String email, String targetType, Long targetId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (targetType.equals("review")) {
            Review review = reviewRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
            ReviewUpvote existingUpvote = reviewUpvoteRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));

            reviewUpvoteRepository.delete(existingUpvote);
        }
        else if (targetType.equals("question")) {
            // Handle comment upvote logic here
        }
        else if (targetType.equals("answer")) {
            // Handle answer upvote logic here
        }
        else if (targetType.equals("discussion")) {
            // Handle discussion upvote logic here
        }
        else if (targetType.equals("discussion_comment")) {
            // Handle discussion comment upvote logic here
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }
}
