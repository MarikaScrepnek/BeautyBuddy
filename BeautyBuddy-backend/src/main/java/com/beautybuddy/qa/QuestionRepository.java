package com.beautybuddy.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	Page<Question> findByProduct_IdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);

	@Query("""
			SELECT DISTINCT q FROM Question q
			LEFT JOIN q.answers a
			WHERE q.product.id = :productId
			  AND q.deletedAt IS NULL
			  AND q.approved = true
			  AND (
				LOWER(q.text) LIKE LOWER(CONCAT('%', :query, '%')) OR
				LOWER(a.text) LIKE LOWER(CONCAT('%', :query, '%'))
			  )
			ORDER BY q.createdAt DESC
	""")
	Page<Question> searchQuestionsByProductAndQuery(@Param("productId") Long productId, @Param("query") String query, Pageable pageable);
}
