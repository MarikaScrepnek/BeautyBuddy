package com.beautybuddy.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "review_upvotes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "review_id"})
)
public class ReviewUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_upvote_id")
    private Integer reviewUpvoteId;

    @Column(name = "account_id", nullable = false)
    private Integer userId;

    @Column(name = "review_id", nullable = false)
    private Integer reviewId;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public ReviewUpvote() {}

    public Integer getReviewUpvoteId() {
        return reviewUpvoteId;
    }
    public void setReviewUpvoteId(Integer reviewUpvoteId) {
        this.reviewUpvoteId = reviewUpvoteId;
    }

    public Integer getUserId() {
        return userId;
    }
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getReviewId() {
        return reviewId;
    }
    public void setReviewId(Integer reviewId) {
        this.reviewId = reviewId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
