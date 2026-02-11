package com.beautybuddy.user;

import com.beautybuddy.common.entity.BaseEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "account_follow",
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_id", "followed_id"})
)
public class UserFollow extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "follower_id", nullable = false)
    private User follower;

    @ManyToOne
    @JoinColumn(name = "followed_id", nullable = false)
    private User followed;

    public User getFollower() {
        return follower;
    }
    public void setFollower(User follower) {
        this.follower = follower;
    }

    public User getFollowed() {
        return followed;
    }
    public void setFollowed(User followed) {
        this.followed = followed;
    }
}
