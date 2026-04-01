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

  Page<Review> findByProduct_IdAndDeletedAtIsNullAndApprovedTrue(
        Long productId,
        Pageable pageable
    );

  Page<Review> findByProduct_IdAndProductShade_ShadeNameIgnoreCaseAndDeletedAtIsNullAndApprovedTrue(
    Long productId,
    String shadeName,
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
    """)
    Page<Review> searchByProductAndText(
        @Param("productId") Long productId,
        @Param("query") String query,
        Pageable pageable
    );

    @Query("""
            SELECT r FROM Review r
            WHERE r.product.id = :productId
              AND r.deletedAt IS NULL
              AND r.approved = true
              AND r.productShade IS NOT NULL
              AND LOWER(r.productShade.shadeName) = LOWER(:shadeName)
              AND (
                LOWER(r.title) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(r.text) LIKE LOWER(CONCAT('%', :query, '%'))
              )
    """)
    Page<Review> searchByProductAndShadeAndText(
        @Param("productId") Long productId,
        @Param("shadeName") String shadeName,
        @Param("query") String query,
        Pageable pageable
    );
}
