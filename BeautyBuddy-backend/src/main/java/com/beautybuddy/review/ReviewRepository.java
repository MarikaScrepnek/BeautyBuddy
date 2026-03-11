package com.beautybuddy.review;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Optional;

import com.beautybuddy.review.entity.Review;

import java.math.BigDecimal;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    Page<Review> findByProduct_IdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(
        Long productId,
        Pageable pageable
    );

    Optional<Review> findByProduct_IdAndProductShade_IdAndUser_IdAndDeletedAtIsNull(Long productId, Long shadeId, Long userId);

    Optional<Review> findByProduct_IdAndUser_IdAndDeletedAtIsNull(Long productId, Long userId);

    @Query("""
        SELECT AVG(r.rating)
        FROM Review r
        WHERE r.product.id = :productId
          AND r.deletedAt IS NULL
          AND r.approved = true
    """)
    BigDecimal findAverageRatingByProductId(Long productId);

    @Query("""
            SELECT r FROM Review r
            WHERE r.product.id = :productId
              AND r.deletedAt IS NULL
              AND r.approved = true
              AND (
                LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(r.text) LIKE LOWER(CONCAT('%', :query, '%'))
              )
            ORDER BY r.createdAt DESC
    """)
    Page<Review> searchByProductAndText(
        @Param("productId") Long productId,
        @Param("query") String query,
        Pageable pageable
    );
}
