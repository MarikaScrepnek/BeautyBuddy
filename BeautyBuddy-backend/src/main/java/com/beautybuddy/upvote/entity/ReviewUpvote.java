package com.beautybuddy.upvote.entity;

import com.beautybuddy.review.Review;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "review_upvote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "review_id"})
)
public class ReviewUpvote extends BaseUpvote {

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
