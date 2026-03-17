package com.beautybuddy.breakout.entity;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.ingredient.entity.Ingredient;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "breakout_list_ingredient",
    uniqueConstraints = @UniqueConstraint(columnNames = {"breakout_list_id", "ingredient_id"})
)
public class BreakoutListIngredient extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "breakout_list_id", nullable = false)
    private BreakoutList breakoutList;

    @ManyToOne
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    public BreakoutList getBreakoutList() {
        return breakoutList;
    }
    public void setBreakoutList(BreakoutList breakoutList) {
        this.breakoutList = breakoutList;
    }

    public Ingredient getIngredient() {
        return ingredient;
    }
    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

}
