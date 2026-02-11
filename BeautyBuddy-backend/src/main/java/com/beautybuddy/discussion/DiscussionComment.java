package com.beautybuddy.discussion;

import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_comment",
    uniqueConstraints = @UniqueConstraint(columnNames = {"id", "discussion_id"})
)
public class DiscussionComment extends UserWrittenEntity{
    
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
