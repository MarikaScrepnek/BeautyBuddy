package com.beautybuddy.upvote.entity;

import com.beautybuddy.review.Review;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_upvote_id")
    private Integer reviewUpvoteId;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public Integer getReviewUpvoteId() {
        return reviewUpvoteId;
    }
    public void setReviewUpvoteId(Integer reviewUpvoteId) {
        this.reviewUpvoteId = reviewUpvoteId;
    }

    public Review getReview() {
        return review;
    }
    public void setReview(Review review) {
        this.review = review;
    }
}
