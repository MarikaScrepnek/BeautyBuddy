package com.beautybuddy.community.activity.entity;

import com.beautybuddy.community.activity.BaseActivityType;
import com.beautybuddy.routine.entity.RoutineImage;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public class RoutineImageActivity extends BaseActivityType {
    @ManyToOne
    @JoinColumn(name = "routine_image_id", nullable = false)
    private RoutineImage routineImage;

    public RoutineImage getRoutineImage() {
        return routineImage;
    }
    public void setRoutineImage(RoutineImage routineImage) {
        this.routineImage = routineImage;
    }
}
