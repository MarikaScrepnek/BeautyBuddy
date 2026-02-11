package com.beautybuddy.notification;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
    name = "notification"
)
public class Notification extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne
    @JoinColumn(name = "actor_id")
    private User actor;

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    @Column(name = "read_at")
    private LocalDateTime readAt;

    @Column(name = "payload", columnDefinition = "TEXT")
    private String payload;

    public User getRecipient() { return recipient; }
    public void setRecipient(User recipient) { this.recipient = recipient; }

    public User getActor() { return actor; }
    public void setActor(User actor) { this.actor = actor; }

    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }

    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }

    public String getPayload() { return payload; }
    public void setPayload(String payload) { this.payload = payload; }
}
