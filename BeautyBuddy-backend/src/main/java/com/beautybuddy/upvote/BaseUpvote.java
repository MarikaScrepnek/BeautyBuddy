package com.beautybuddy.upvote;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class BaseUpvote extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private User user;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}

// upvotes