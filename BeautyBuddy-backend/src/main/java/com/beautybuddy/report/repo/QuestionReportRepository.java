package com.beautybuddy.report.repo;

import com.beautybuddy.report.entity.QuestionReport;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.qa.Question;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionReportRepository extends JpaRepository<QuestionReport, Long> {
    Optional<QuestionReport> findByUserAndQuestion(User user, Question question);
    List<QuestionReport> findAllByUser(User user);
    
}
