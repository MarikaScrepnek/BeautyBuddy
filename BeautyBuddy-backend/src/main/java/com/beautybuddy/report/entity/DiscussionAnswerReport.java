package com.beautybuddy.report.entity;

import com.beautybuddy.discussion.DiscussionAnswer;
import com.beautybuddy.report.BaseReport;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "discussion_answer_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "answer_id"})
)
public class DiscussionAnswerReport extends BaseReport {
    
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    DiscussionAnswer discussionAnswer;

    public DiscussionAnswer getDiscussionAnswer() {
        return discussionAnswer;
    }
    public void setDiscussionAnswer(DiscussionAnswer discussionAnswer) {
        this.discussionAnswer = discussionAnswer;
    }
}
