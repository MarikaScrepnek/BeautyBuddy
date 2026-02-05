package com.beautybuddy.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.LocalDateTime;

@Entity
@Table(name = "review_image")
public class ReviewImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewImageId;

    @ManyToOne
    @JoinColumn(name = "review_id", nullable = false)
    private Review review;

    @Column(name = "image_link", nullable = false)
    private String imageLink;

    @Column(name = "uploaded_at", nullable = false)
    private LocalDateTime uploadedAt;

    // Getters and Setters
    public ReviewImage() {
    }

    public int getReviewImageId() {
        return reviewImageId;
    }
    public void setReviewImageId(int reviewImageId) {
        this.reviewImageId = reviewImageId;
    }

    public Review getReview() {
        return review;
    }
    public void setReview(Review review) {
        this.review = review;
    }

    public String getImageLink() {
        return imageLink;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
}
