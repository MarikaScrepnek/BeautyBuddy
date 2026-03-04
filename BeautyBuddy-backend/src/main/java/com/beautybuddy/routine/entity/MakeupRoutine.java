package com.beautybuddy.routine.entity;


import java.util.Set;

import com.beautybuddy.routine.OccasionEnum;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "makeup_routine")
public class MakeupRoutine {
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
    @Column(name = "occasion", nullable = false)
    private OccasionEnum occasion;

    @Column(name = "name", nullable = true)
    private String name;

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

    public OccasionEnum getOccasion() {
        return occasion;
    }
    public void setOccasion(OccasionEnum occasion) {
        this.occasion = occasion;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @OneToMany(
        mappedBy = "makeupRoutine",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<MakeupRoutineItem> makeupRoutineItems;

    public Set<MakeupRoutineItem> getItems() {
        return makeupRoutineItems;
    }
    public void setItems(Set<MakeupRoutineItem> makeupRoutineItems) {
        this.makeupRoutineItems = makeupRoutineItems;
    }
}
