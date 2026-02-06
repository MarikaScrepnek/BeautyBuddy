package com.beautybuddy.upvote.entity;

import com.beautybuddy.qa.Question;
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
    name = "question_upvote",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "question_id"})
)
public class QuestionUpvote extends BaseUpvote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_upvote_id")
    private Integer questionUpvoteId;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Integer getQuestionUpvoteId() {
        return questionUpvoteId;
    }
    public void setQuestionUpvoteId(Integer questionUpvoteId) {
        this.questionUpvoteId = questionUpvoteId;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
}
