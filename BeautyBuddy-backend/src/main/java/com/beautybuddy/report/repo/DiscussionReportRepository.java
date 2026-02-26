package com.beautybuddy.report.repo;

import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.report.entity.DiscussionReport;
import com.beautybuddy.user.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DiscussionReportRepository extends JpaRepository<DiscussionReport, Long> {
    Optional<DiscussionReport> findByUserAndDiscussion(User user, Discussion discussion);
}
