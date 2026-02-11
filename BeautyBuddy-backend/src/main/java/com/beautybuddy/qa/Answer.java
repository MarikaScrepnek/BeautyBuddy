package com.beautybuddy.qa;

import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "answer"
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
