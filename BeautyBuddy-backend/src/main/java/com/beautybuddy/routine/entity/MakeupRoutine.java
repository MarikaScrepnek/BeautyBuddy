package com.beautybuddy.routine.entity;


import com.beautybuddy.routine.OccasionEnum;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "makeup_routine")
public class MakeupRoutine {
    @Id
    @OneToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "occasion", nullable = false)
    private OccasionEnum occasion;

    @Column(name = "name", nullable = true)
    private String name;
}
