package com.beautybuddy.qa;

import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "answer",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"question_id", "account_id"})
    }
)
public class Answer extends UserWrittenEntity{
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
}
