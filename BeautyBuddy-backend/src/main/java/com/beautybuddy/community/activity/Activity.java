package com.beautybuddy.community.activity;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "activity"
)
public class Activity extends BaseEntity{
    
    @ManyToOne
    @Column(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "type", nullable = false)
    private ActivityType type;

    @Column(name = "payload", nullable = false)
    private String payload;

    public User getActor() {
        return actor;
    }
    public void setActor(User actor) {
        this.actor = actor;
    }

    public ActivityType getType() {
        return type;
    }
    public void setType(ActivityType type) {
        this.type = type;
    }

    public String getPayload() {
        return payload;
    }
    public void setPayload(String payload) {
        this.payload = payload;
    }
}
