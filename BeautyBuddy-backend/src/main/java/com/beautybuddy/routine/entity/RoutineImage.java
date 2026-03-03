package com.beautybuddy.routine.entity;

import com.beautybuddy.common.entity.UserImageEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table (
    name = "routine_image",
    uniqueConstraints = @UniqueConstraint(columnNames = {"routine_id", "image_link"})
)
public class RoutineImage extends UserImageEntity {
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    public Routine getRoutine() {
        return routine;
    }
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }
}
