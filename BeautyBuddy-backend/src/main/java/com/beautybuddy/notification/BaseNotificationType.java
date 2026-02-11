package com.beautybuddy.notification;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

public abstract class BaseNotificationType {
    @Id
    @ManyToOne
    @JoinColumn(name = "notification_id", nullable = false)
    private Notification notification;
}
