package com.beautybuddy.upvote.entity;

import com.beautybuddy.discussion.Discussion;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_upvote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_id"})
)
public class DiscussionUpvote extends BaseUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discussion_upvote_id")
    private Integer discussionUpvoteId;

    @ManyToOne
    @JoinColumn(name = "discussion_id", nullable = false)
    private Discussion discussion;

    public Integer getDiscussionUpvoteId() {
        return discussionUpvoteId;
    }
    public void setDiscussionUpvoteId(Integer discussionUpvoteId) {
        this.discussionUpvoteId = discussionUpvoteId;
    }

    public Discussion getDiscussion() {
        return discussion;
    }
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }
}
