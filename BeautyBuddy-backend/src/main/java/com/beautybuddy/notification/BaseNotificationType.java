package com.beautybuddy.notification;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToOne;

@MappedSuperclass
public abstract class BaseNotificationType extends ForeignKeyIdEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;

    public Notification getNotification() {
        return notification;
    }
    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    @Override
    protected Long getForeignKeyId() {
        return notification != null ? notification.getId() : null;
    }
}
