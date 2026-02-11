package com.beautybuddy.routine;

import com.beautybuddy.common.entity.SoftDeletableEntity;
import com.beautybuddy.user.User;
import com.beautybuddy.category.Category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "routine",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"account_id", "name"})
    }
)
public class Routine extends SoftDeletableEntity {
    
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "notes")
    private String notes;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
