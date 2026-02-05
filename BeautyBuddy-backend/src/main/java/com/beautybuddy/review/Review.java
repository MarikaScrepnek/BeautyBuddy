package com.beautybuddy.review;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;
import com.beautybuddy.user.User;

@Entity
@Table(name = "review",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "product_id"})
    }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int review_id;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_shade_id", nullable = true)
    private ProductShade productShade;

    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "review_text", nullable = true)
    private String reviewText;

    @Column(name = "helpful_count", nullable = false)
    private int helpfulCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "reported_count", nullable = false)
    private int reportedCount;

    @Column(name = "approved", nullable = false)
    private boolean approved;

    @OneToMany(
        mappedBy = "review",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ReviewImage> images = new ArrayList<>();

    public Review() {
    }

    // Getters and Setters
    public int getReview_id() {
        return review_id;
    }
    public void setReview_id(int review_id) {
        this.review_id = review_id;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductShade getProductShade() {
        return productShade;
    }
    public void setProductShade(ProductShade productShade) {
        this.productShade = productShade;
    }

    public BigDecimal getRating() {
        return rating;
    }
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }

    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public int getHelpfulCount() {
        return helpfulCount;
    }
    public void setHelpfulCount(int helpfulCount) {
        this.helpfulCount = helpfulCount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public int getReportedCount() {
        return reportedCount;
    }
    public void setReportedCount(int reportedCount) {
        this.reportedCount = reportedCount;
    }

    public boolean isApproved() {
        return approved;
    }
    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public List<ReviewImage> getImages() {
        return images;
    }
    public void setImages(List<ReviewImage> images) {
        this.images = images;
    }
}
