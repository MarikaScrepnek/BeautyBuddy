package com.beautybuddy.repository;

import com.beautybuddy.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    @Query("SELECT r FROM Review r WHERE r.product.product_id = :productId AND r.deletedAt IS NULL AND r.approved = true")
    Page<Review> findByProductId(@Param("productId") int productId, Pageable pageable);

    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.product_id = :productId AND r.deletedAt IS NULL AND r.approved = true")
    Double findAverageRatingByProductId(@Param("productId") int productId);
}
