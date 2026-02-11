package com.beautybuddy.community.activity.entity;

import com.beautybuddy.community.activity.BaseActivityType;
import com.beautybuddy.routine.RoutineItem;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (name = "routine_item_activity")
public class RoutineItemActivity extends BaseActivityType {
    @ManyToOne
    @JoinColumn(name = "routine_item_id", nullable = false)
    private RoutineItem routineItem;

    public RoutineItem getRoutineItem() {
        return routineItem;
    }
    public void setRoutineItem(RoutineItem routineItem) {
        this.routineItem = routineItem;
    }
}
