package com.beautybuddy.routine.entity;

import com.beautybuddy.user.entity.User;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "haircare_routine",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id"})
)
public class HaircareRoutine {
    @Id
    @OneToOne
    @JoinColumn(name = "routine_id", nullable = false)
    private Routine routine;

    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    public Routine getRoutine() {
        return routine;
    }
    public void setRoutine(Routine routine) {
        this.routine = routine;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
