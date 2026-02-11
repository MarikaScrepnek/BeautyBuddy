package com.beautybuddy.common.entity;

import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserWrittenEntity extends SoftDeletableEntity {
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "text")
    private String text;

    @Column(name = "approved", nullable = false)
    private boolean approved = false;

    @Column(name = "reported_count", nullable = false)
    private int reportedCount = 0;

    @Column(name = "upvote_count", nullable = false)
    private int upvoteCount = 0;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public int getReportedCount() {
        return reportedCount;
    }
    public void setReportedCount(int reportedCount) {
        this.reportedCount = reportedCount;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }
    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }
}

// review, question, answer, discussion, discussion_comment