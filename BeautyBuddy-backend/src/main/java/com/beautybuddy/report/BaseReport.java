package com.beautybuddy.report;

import java.time.LocalDateTime;

import com.beautybuddy.common.entity.BaseEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import org.hibernate.annotations.ColumnTransformer;

@MappedSuperclass
public abstract class BaseReport extends BaseEntity{
    @ManyToOne
    @JoinColumn(name = "account_id", nullable = false, updatable = false)
    private User user;

    @Column(name = "reason", nullable = true)
    private String reason;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnTransformer(write = "?::report_status_enum")
    private ReportStatus status = ReportStatus.OPEN;

    @Column(name = "resolved_at", nullable = true)
    private LocalDateTime resolvedAt;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public String getReason() {
        return reason;
    }
    public void setReason(String reason) {
        this.reason = reason;
    }

    public ReportStatus getStatus() {
        return status;
    }
    public void setStatus(ReportStatus status) {
        this.status = status;
    }
    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}

// reports