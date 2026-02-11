package com.beautybuddy.routine;

import com.beautybuddy.common.entity.UserImageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table (
    name = "routine_image",
    uniqueConstraints = @UniqueConstraint(columnNames = {"routine_id", "image_link"})
)
public class RoutineImage extends UserImageEntity {
    
}
