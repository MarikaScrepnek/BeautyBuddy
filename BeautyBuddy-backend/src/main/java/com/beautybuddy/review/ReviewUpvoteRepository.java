package com.beautybuddy.review;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.upvote.ReviewUpvote;

public interface ReviewUpvoteRepository extends JpaRepository<ReviewUpvote, Integer> {
    
}
