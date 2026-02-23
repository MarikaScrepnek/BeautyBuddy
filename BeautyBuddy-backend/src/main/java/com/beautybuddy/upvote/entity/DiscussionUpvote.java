package com.beautybuddy.upvote.entity;

import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_upvote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_id"})
)
public class DiscussionUpvote extends BaseUpvote {

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
