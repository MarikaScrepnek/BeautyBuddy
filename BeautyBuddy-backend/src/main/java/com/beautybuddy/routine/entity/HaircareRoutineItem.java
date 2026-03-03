package com.beautybuddy.routine.entity;

import com.beautybuddy.routine.RoutineItem;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "haircare_routine_item")
public class HaircareRoutineItem extends RoutineItem {
    @ManyToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private HaircareRoutine haircareRoutine;

    @Column(name = "occurrence")
    private String occurrence;

    public HaircareRoutine getHaircareRoutine() {
        return haircareRoutine;
    }
    public void setHaircareRoutine(HaircareRoutine haircareRoutine) {
        this.haircareRoutine = haircareRoutine;
    }

    public String getOccurrence() {
        return occurrence;
    }
    public void setOccurrence(String occurrence) {
        this.occurrence = occurrence;
    }
}
