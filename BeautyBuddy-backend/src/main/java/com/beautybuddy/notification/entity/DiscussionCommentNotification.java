package com.beautybuddy.notification.entity;

import com.beautybuddy.discussion.Discussion;
import com.beautybuddy.discussion.DiscussionComment;
import com.beautybuddy.notification.BaseNotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "discussion_comment_notification"
)
public class DiscussionCommentNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    @ManyToOne
    @JoinColumn(name = "discussion_comment_id", nullable = false)
    private DiscussionComment comment;

    public Discussion getDiscussion() {
        return discussion;
    }
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public DiscussionComment getComment() {
        return comment;
    }
    public void setComment(DiscussionComment comment) {
        this.comment = comment;
    }
}
