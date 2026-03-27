package com.beautybuddy.review.entity;

import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.entity.ProductShade;
import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OrderBy;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "review"
)
public class Review extends UserWrittenEntity{

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "product_shade_id")
    private ProductShade productShade;
    
    @Column(name = "rating", nullable = false)
    private BigDecimal rating;

    @Column(name = "title")
    private String title;

    @OneToMany(
        mappedBy = "review",
        cascade = CascadeType.ALL,
        orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private List<ReviewImage> reviewImages = new ArrayList<>();

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

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public List<ReviewImage> getReviewImages() {
        return reviewImages;
    }
    public void setReviewImages(List<ReviewImage> reviewImages) {
        this.reviewImages = reviewImages;
    }
}
