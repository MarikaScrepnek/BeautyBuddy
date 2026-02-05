package com.beautybuddy.review;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewUpvoteRepository extends JpaRepository<ReviewUpvote, Integer> {
    
}
