package com.beautybuddy.notification.entity;

import com.beautybuddy.notification.BaseNotificationType;
import com.beautybuddy.qa.Question;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "question_upvoted_notification"
)
public class QuestionUpvotedNotification extends BaseNotificationType {
    
    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    Question question;

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
}
