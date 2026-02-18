package com.beautybuddy.qa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuestionRepository extends JpaRepository<Question, Long> {
	Page<Question> findByProduct_IdAndDeletedAtIsNullAndApprovedTrueOrderByCreatedAtDesc(Long productId, Pageable pageable);
}
