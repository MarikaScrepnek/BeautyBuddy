package com.beautybuddy.notification.entity;

import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.notification.BaseNotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "discussion_comment_upvoted_notification"
)
public class DiscussionCommentUpvotedNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "discussion_comment_id", nullable = false)
    private DiscussionComment discussionComment;

    public DiscussionComment getDiscussionComment() {
        return discussionComment;
    }
    public void setDiscussionComment(DiscussionComment discussionComment) {
        this.discussionComment = discussionComment;
    }
}