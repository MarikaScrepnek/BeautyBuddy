package com.beautybuddy.repository;

import com.beautybuddy.model.Review;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {
    Optional<Review> findByProductId(int productId);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.productId = :productId AND r.approved = true")
    Optional<Double> findAverageRatingByProductId(int productId);
}
