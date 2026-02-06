package com.beautybuddy.report.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.report.entity.ReviewReport;

public interface ReviewReportRepository extends JpaRepository<ReviewReport, Integer> {
}
