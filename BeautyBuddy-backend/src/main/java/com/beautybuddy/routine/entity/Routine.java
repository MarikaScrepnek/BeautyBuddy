package com.beautybuddy.routine.entity;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.JdbcType;
import org.hibernate.dialect.PostgreSQLEnumJdbcType;

import com.beautybuddy.category.Category;
import com.beautybuddy.common.entity.SoftDeletableEntity;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "routine")
public class Routine extends SoftDeletableEntity {

    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @Column(name = "name", nullable = true)
    private String name;

    @Column(name = "notes", nullable = true)
    private String notes;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "time_of_day_enum", name = "time_of_day", nullable = true)
    private TimeOfDayEnum timeOfDay;

    @Enumerated
    @JdbcType(PostgreSQLEnumJdbcType.class)
    @Column(columnDefinition = "occasion_enum", name = "occasion", nullable = true)
    private OccasionEnum occasion;

    @Column(name = "is_system", nullable = false)
    private Boolean isSystem = false;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getNotes() {
        return notes;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public TimeOfDayEnum getTimeOfDay() {
        return timeOfDay;
    }
    public void setTimeOfDay(TimeOfDayEnum timeOfDay) {
        this.timeOfDay = timeOfDay;
    }

    public OccasionEnum getOccasion() {
        return occasion;
    }
    public void setOccasion(OccasionEnum occasion) {
        this.occasion = occasion;
    }

    public Boolean getIsSystem() {
        return isSystem;
    }
    public void setIsSystem(Boolean isSystem) {
        this.isSystem = isSystem;
    }

    @OneToMany(
        mappedBy = "routine",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<RoutineItem> items = new HashSet<>();

    public Set<RoutineItem> getItems() {
        return items;
    }
    public void setItems(Set<RoutineItem> items) {
        this.items = items;
    }

}
