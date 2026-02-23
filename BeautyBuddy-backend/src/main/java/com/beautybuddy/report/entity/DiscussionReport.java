package com.beautybuddy.report.entity;

import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.report.BaseReport;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_id"})
)
public class DiscussionReport extends BaseReport {

    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    public Discussion getDiscussion() {
        return discussion;
    }
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
}
