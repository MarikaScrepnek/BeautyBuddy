package com.beautybuddy.upvote.entity;

import com.beautybuddy.discussion.DiscussionComment;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_comment_upvote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_comment_id"})
)
public class DiscussionCommentUpvote extends BaseUpvote {

    @ManyToOne
    @JoinColumn(name = "discussion_comment_id", nullable = false)
    private DiscussionComment discussionAnswer;

    public DiscussionComment getDiscussionAnswer() {
        return discussionAnswer;
    }
    public void setDiscussionAnswer(DiscussionComment discussionAnswer) {
        this.discussionAnswer = discussionAnswer;
    }
}
