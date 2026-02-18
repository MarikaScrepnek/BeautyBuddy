package com.beautybuddy.report.entity;

import com.beautybuddy.report.BaseReport;
import com.beautybuddy.review.entity.Review;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "review_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "review_id"})
)
public class ReviewReport extends BaseReport {

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
