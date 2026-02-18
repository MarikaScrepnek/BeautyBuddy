package com.beautybuddy.report.repo;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.report.entity.ReviewReport;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.user.User;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Long> {
    Optional<ReviewReport> findByUserAndReview(User user, Review review);
}
