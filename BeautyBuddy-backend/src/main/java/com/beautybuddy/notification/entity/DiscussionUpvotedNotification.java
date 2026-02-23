package com.beautybuddy.notification.entity;

import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.notification.BaseNotificationType;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "discussion_upvoted_notification"
)
public class DiscussionUpvotedNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    public Discussion getDiscussion() {
        return discussion;
    }
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
}
