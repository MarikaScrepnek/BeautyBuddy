package com.beautybuddy.notification.entity;

import com.beautybuddy.notification.BaseNotificationType;
import com.beautybuddy.qa.Answer;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "answer_upvoted_notification"
)
public class AnswerUpvotedNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "answer_id", nullable = false)
    private Answer answer;

    public Answer getAnswer() {
        return answer;
    }
    public void setAnswer(Answer answer) {
        this.answer = answer;
    }
}
