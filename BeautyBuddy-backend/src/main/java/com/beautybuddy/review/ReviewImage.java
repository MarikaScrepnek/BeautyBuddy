package com.beautybuddy.review;

import com.beautybuddy.common.entity.UserImageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "review_image",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"review_id", "image_link"})
    }
)
public class ReviewImage extends UserImageEntity{

}
