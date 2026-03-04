package com.beautybuddy.routine.entity;

import com.beautybuddy.routine.TimeOfDayEnum;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "skincare_routine",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "time_of_day"})
)
public class SkincareRoutine {
    @Id
    @Column(name = "routine_id")
    private Long routineId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "routine_id")
    private Routine routine;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "time_of_day", nullable = false)
    private TimeOfDayEnum timeOfDay;
}
