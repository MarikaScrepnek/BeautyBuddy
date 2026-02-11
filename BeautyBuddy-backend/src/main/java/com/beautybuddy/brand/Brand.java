package com.beautybuddy.brand;

import com.beautybuddy.common.entity.UpdatableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "brand")
public class Brand extends UpdatableEntity {

    @Column(unique = true, nullable = false)
    private String name;

    @Column(name = "is_discontinued", nullable = false)
    private boolean isDiscontinued = false;
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isDiscontinued() { return isDiscontinued; }
    public void setDiscontinued(boolean discontinued) { this.isDiscontinued = discontinued; }
}

