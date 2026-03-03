package com.beautybuddy.routine.entity;

import com.beautybuddy.routine.RoutineItem;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "makeup_routine_item")
public class MakeupRoutineItem extends RoutineItem {
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private MakeupRoutine makeupRoutine;

    public MakeupRoutine getMakeupRoutine() {
        return makeupRoutine;
    }
    public void setMakeupRoutine(MakeupRoutine makeupRoutine) {
        this.makeupRoutine = makeupRoutine;
    }
}
