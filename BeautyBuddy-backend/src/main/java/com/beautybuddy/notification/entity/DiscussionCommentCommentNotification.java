package com.beautybuddy.notification.entity;

import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.notification.BaseNotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "discussion_comment_comment_notification")
public class DiscussionCommentCommentNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "parent_discussion_comment_id", nullable = false)
    private DiscussionComment parentDiscussionComment;

    @ManyToOne
    @JoinColumn(name = "discussion_comment_id", nullable = false)
    private DiscussionComment discussionComment;

    public DiscussionComment getParentDiscussionComment() {
        return parentDiscussionComment;
    }
    public void setParentDiscussionComment(DiscussionComment parentDiscussionComment) {
        this.parentDiscussionComment = parentDiscussionComment;
    }

    public DiscussionComment getDiscussionComment() {
        return discussionComment;
    }
    public void setDiscussionComment(DiscussionComment discussionComment) {
        this.discussionComment = discussionComment;
    }
}
