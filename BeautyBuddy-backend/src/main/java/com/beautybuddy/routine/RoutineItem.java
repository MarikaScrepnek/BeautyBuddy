package com.beautybuddy.routine;

import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;

import java.time.LocalDateTime;

import com.beautybuddy.common.entity.BaseEntity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Column;

@MappedSuperclass
public abstract class RoutineItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shade_id")
    private ProductShade shade;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "notes", nullable = true)
    private String notes;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom = LocalDateTime.now();

    @Column(name = "valid_to", nullable = true)
    private LocalDateTime validTo;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public ProductShade getShade() {
        return shade;
    }
    public void setShade(ProductShade shade) {
        this.shade = shade;
    }

    public int getStepOrder() {
        return stepOrder;
    }
    public void setStepOrder(int stepOrder) {
        this.stepOrder = stepOrder;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }
    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidTo() {
        return validTo;
    }
    public void setValidTo(LocalDateTime validTo) {
        this.validTo = validTo;
    }
}
