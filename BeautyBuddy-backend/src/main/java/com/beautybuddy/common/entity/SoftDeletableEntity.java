package com.beautybuddy.common.entity;

import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import jakarta.persistence.Column;

@MappedSuperclass
public abstract class SoftDeletableEntity extends UpdatableEntity {
    @Column(name = "deleted_at", nullable = true, updatable = true)
    private LocalDateTime deletedAt;

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public boolean isDeleted() {
        return deletedAt != null;
    }
}

// account, routine, public_community_post