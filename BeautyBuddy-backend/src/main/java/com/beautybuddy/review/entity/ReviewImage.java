package com.beautybuddy.review.entity;

import com.beautybuddy.common.entity.UserImageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "review_image",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "image_link"})
    }
)
public class ReviewImage extends UserImageEntity{

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    public Review getReview() { return review; }
    public void setReview(Review review) { this.review = review; }
}
