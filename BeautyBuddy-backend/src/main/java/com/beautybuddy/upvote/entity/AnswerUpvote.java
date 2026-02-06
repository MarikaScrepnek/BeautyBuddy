package com.beautybuddy.upvote.entity;

import com.beautybuddy.qa.Answer;
import com.beautybuddy.upvote.BaseUpvote;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "answer_upvotes",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "answer_id"})
)
public class AnswerUpvote extends BaseUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "answer_upvote_id")
    private Integer answerUpvoteId;

    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    public Integer getAnswerUpvoteId() {
        return answerUpvoteId;
    }
    public void setAnswerUpvoteId(Integer answerUpvoteId) {
        this.answerUpvoteId = answerUpvoteId;
    }

    public Answer getAnswer() {
        return answer;
    }
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
