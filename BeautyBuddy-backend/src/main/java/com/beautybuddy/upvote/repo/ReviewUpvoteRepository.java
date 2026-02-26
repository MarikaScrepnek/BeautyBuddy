package com.beautybuddy.upvote.repo;

import java.util.Optional;

import com.beautybuddy.review.entity.Review;
import com.beautybuddy.upvote.BaseUpvoteRepository;
import com.beautybuddy.upvote.entity.ReviewUpvote;
import com.beautybuddy.user.entity.User;

public interface ReviewUpvoteRepository extends BaseUpvoteRepository<ReviewUpvote, Integer> {
    Optional<ReviewUpvote> findByUserAndReview(User user, Review review);
}