package com.beautybuddy.ingredient;

import com.beautybuddy.common.entity.UpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (
    name = "ingredient"
)
public class Ingredient extends UpdatableEntity{

    @Column(name = "name", nullable = false, unique = true, columnDefinition = "CITEXT")
    private String name;

    @Column(name = "canonical_id")
    private Long canonicalId;

    @Column(name = "is_common_allergen", nullable = false)
    private boolean isCommonAllergen = false;

    @Column(name = "is_common_irritant", nullable = false)
    private boolean isCommonIrritant = false;

    @Column(name = "is_fragrance", nullable = false)
    private boolean isFragrance = false;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Long getCanonicalId() { return canonicalId; }
    public void setCanonicalId(Long canonicalId) { this.canonicalId = canonicalId; }

    public boolean isCommonAllergen() { return isCommonAllergen; }
    public void setCommonAllergen(boolean commonAllergen) { isCommonAllergen = commonAllergen; }

    public boolean isCommonIrritant() { return isCommonIrritant; }
    public void setCommonIrritant(boolean commonIrritant) { isCommonIrritant = commonIrritant; }

    public boolean isFragrance() { return isFragrance; }
    public void setFragrance(boolean fragrance) { isFragrance = fragrance; }
}