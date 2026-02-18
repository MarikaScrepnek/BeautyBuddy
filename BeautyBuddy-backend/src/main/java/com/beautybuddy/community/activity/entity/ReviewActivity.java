package com.beautybuddy.community.activity.entity;

import com.beautybuddy.community.activity.BaseActivityType;
import com.beautybuddy.review.entity.Review;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "review_activity")
public class ReviewActivity extends BaseActivityType {
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
