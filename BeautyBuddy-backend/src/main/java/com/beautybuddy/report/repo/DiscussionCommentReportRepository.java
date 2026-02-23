package com.beautybuddy.report.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.report.entity.DiscussionCommentReport;
import com.beautybuddy.user.User;

public interface DiscussionCommentReportRepository extends JpaRepository<DiscussionCommentReport, Long> {
    Optional<DiscussionCommentReport> findByUserAndDiscussionComment(User user, DiscussionComment discussionComment);
}
