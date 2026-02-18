package com.beautybuddy.report.repo;

import com.beautybuddy.report.entity.QuestionReport;
import com.beautybuddy.qa.Question;
import com.beautybuddy.user.User;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionReportRepository extends JpaRepository<QuestionReport, Long> {
    Optional<QuestionReport> findByUserAndQuestion(User user, Question question);
    
}
