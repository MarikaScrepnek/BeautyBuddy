package com.beautybuddy.upvote.entity;

import com.beautybuddy.discussion.DiscussionAnswer;
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
    name = "discussion_answer_upvotes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "discussion_answer_id"})
)
public class DiscussionAnswerUpvote extends BaseUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discussion_answer_upvote_id")
    private Integer discussionAnswerUpvoteId;

    @ManyToOne
    @JoinColumn(name = "discussion_answer_id", nullable = false)
    private DiscussionAnswer discussionAnswer;

    public Integer getDiscussionAnswerUpvoteId() {
        return discussionAnswerUpvoteId;
    }
    public void setDiscussionAnswerUpvoteId(Integer discussionAnswerUpvoteId) {
        this.discussionAnswerUpvoteId = discussionAnswerUpvoteId;
    }

    public DiscussionAnswer getDiscussionAnswer() {
        return discussionAnswer;
    }
    public void setDiscussionAnswer(DiscussionAnswer discussionAnswer) {
        this.discussionAnswer = discussionAnswer;
    }
}
