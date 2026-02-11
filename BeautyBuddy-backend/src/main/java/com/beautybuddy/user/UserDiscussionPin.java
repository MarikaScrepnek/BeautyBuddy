package com.beautybuddy.user;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.discussion.Discussion;

import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
@Table(
    name = "user_discussion_pin",
    uniqueConstraints = @UniqueConstraint(columnNames = {"discussion_id", "account_id"})
)
public class UserDiscussionPin extends BaseEntity {
    
    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    public Discussion getDiscussion() { return discussion; }
    public void setDiscussion(Discussion discussion) { this.discussion = discussion; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
