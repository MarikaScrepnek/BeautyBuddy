package com.beautybuddy.discussion;

import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Column;
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

    @ManyToOne
    @JoinColumn(name = "parent_discussion_comment_id")
    private DiscussionComment parentDiscussionComment;

    @Column(name = "reply_count", nullable = false)
    private int replyCount = 0;

    public Discussion getDiscussion() {
        return discussion;
    }
    public void setDiscussion(Discussion discussion) {
        this.discussion = discussion;
    }

    public DiscussionComment getParentDiscussionComment() {
        return parentDiscussionComment;
    }
    public void setParentDiscussionComment(DiscussionComment parentDiscussionComment) {
        this.parentDiscussionComment = parentDiscussionComment;
    }

    public int getReplyCount() {
        return replyCount;
    }
    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
}
