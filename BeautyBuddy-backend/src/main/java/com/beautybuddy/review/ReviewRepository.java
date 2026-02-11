package com.beautybuddy.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.math.BigDecimal;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProduct_IdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(
        Long productId,
        Pageable pageable
    );

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.product.id = :productId
          AND r.deletedAt IS NULL
          AND r.approved = true
    """)
    BigDecimal findAverageRatingByProductId(Long productId);
}
