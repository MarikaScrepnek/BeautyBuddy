package com.beautybuddy.breakout.entity;

import java.time.LocalDateTime;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;
import com.beautybuddy.user.entity.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Table(
    name = "breakout_list",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id"})
)
@Entity
public class BreakoutList extends ForeignKeyIdEntity{
    @Id
    @OneToOne
    @JoinColumn(name = "account_id", nullable = false)
    private User user;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updated_at;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getUpdatedAt() {
        return updated_at;
    }
    public void setUpdatedAt(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    @Override
    protected Long getForeignKeyId() {
        return user.getId();
    }
}
