package com.beautybuddy.breakout.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Table(
    name = "breakout_list",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id"})
)
@Entity

public class BreakoutList extends ForeignKeyIdEntity{
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    @OneToMany(
        mappedBy = "breakoutList",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<BreakoutListIngredient> ingredients = new HashSet<>();

    @OneToMany(
        mappedBy = "breakoutList",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<BreakoutListProduct> products = new HashSet<>();

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    public Set<BreakoutListIngredient> getIngredients() {
        return ingredients;
    }
    public void setIngredients(Set<BreakoutListIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public Set<BreakoutListProduct> getProducts() {
        return products;
    }
    public void setProducts(Set<BreakoutListProduct> products) {
        this.products = products;
    }

    @Override
    protected Long getForeignKeyId() {
        return user.getId();
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
}
