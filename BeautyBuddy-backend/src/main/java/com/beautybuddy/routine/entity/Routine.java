package com.beautybuddy.routine.entity;

import java.time.LocalDateTime;

import com.beautybuddy.category.Category;
import com.beautybuddy.common.entity.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "routine")
public class Routine extends SoftDeletableEntity {
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "notes", nullable = true)
    private String notes;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom = LocalDateTime.now();

    @Column(name = "valid_to", nullable = true)
    private LocalDateTime validTo;

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
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
