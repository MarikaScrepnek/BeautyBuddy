package com.beautybuddy.routine.entity;

import com.beautybuddy.routine.RoutineItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "skincare_routine_item")
public class SkincareRoutineItem extends RoutineItem {
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private SkincareRoutine skincareRoutine;

    @Column(name = "occurrence")
    private String occurrence;

    public SkincareRoutine getSkincareRoutine() {
        return skincareRoutine;
    }
    public void setSkincareRoutine(SkincareRoutine skincareRoutine) {
        this.skincareRoutine = skincareRoutine;
    }

    public String getOccurrence() {
        return occurrence;
    }
    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }
}
