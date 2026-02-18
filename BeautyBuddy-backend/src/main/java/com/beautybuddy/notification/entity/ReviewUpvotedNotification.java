package com.beautybuddy.notification.entity;

import com.beautybuddy.notification.BaseNotificationType;
import com.beautybuddy.review.entity.Review;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "review_upvoted_notification"
)
public class ReviewUpvotedNotification extends BaseNotificationType {
    
    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public Review getReview() {
        return review;
    }
    public void setReview(Review review) {
        this.review = review;
    }
}
