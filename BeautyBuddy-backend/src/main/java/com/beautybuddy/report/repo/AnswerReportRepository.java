package com.beautybuddy.report.repo;

import com.beautybuddy.report.entity.AnswerReport;
import com.beautybuddy.qa.Answer;
import com.beautybuddy.user.User;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerReportRepository extends JpaRepository<AnswerReport, Long> {
    Optional<AnswerReport> findByUserAndAnswer(User user, Answer answer);
    List<AnswerReport> findAllByUser(User user);
}
