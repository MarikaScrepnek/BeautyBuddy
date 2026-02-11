package com.beautybuddy.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass
public abstract class UserImageEntity extends BaseEntity {
    @Column(name = "image_link", nullable = false)
    private String imageLink;

    @Column(name = "display_order", nullable = false)
    private int displayOrder;

    public String getImageLink() {
        return imageLink;
    }
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public int getDisplayOrder() {
        return displayOrder;
    }
    public void setDisplayOrder(int displayOrder) {
        this.displayOrder = displayOrder;
    }
}

//routine_image, review_image