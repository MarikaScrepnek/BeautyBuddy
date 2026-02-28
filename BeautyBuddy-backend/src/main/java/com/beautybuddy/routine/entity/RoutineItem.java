package com.beautybuddy.routine.entity;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.product.Product;
import com.beautybuddy.product.ProductShade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "routine_item"
)
public class RoutineItem extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "shade_id")
    private ProductShade shade;

    @Column(name = "step_order", nullable = false)
    private int stepOrder;

    @Column(name = "notes")
    private String notes;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to")
    private LocalDateTime validTo;

    public Routine getRoutine() { return routine; }
    public void setRoutine(Routine routine) { this.routine = routine; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public ProductShade getShade() { return shade; }
    public void setShade(ProductShade shade) { this.shade = shade; }

    public int getStepOrder() { return stepOrder; }
    public void setStepOrder(int stepOrder) { this.stepOrder = stepOrder; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getValidFrom() { return validFrom; }
    public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }

    public LocalDateTime getValidTo() { return validTo; }
    public void setValidTo(LocalDateTime validTo) { this.validTo = validTo; }
}
