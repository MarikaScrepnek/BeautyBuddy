package com.beautybuddy.report.entity;

import com.beautybuddy.discussion.DiscussionComment;
import com.beautybuddy.report.BaseReport;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_comment_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_comment_id"})
)
public class DiscussionCommentReport extends BaseReport {
    
    @ManyToOne
    @JoinColumn(name = "discussion_comment_id", nullable = false)
    DiscussionComment discussionComment;

    public DiscussionComment getDiscussionComment() {
        return discussionComment;
    }
    public void setDiscussionComment(DiscussionComment discussionComment) {
        this.discussionComment = discussionComment;
    }
}
