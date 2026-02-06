package com.beautybuddy.report.entity;

import com.beautybuddy.qa.Answer;
import com.beautybuddy.report.BaseReport;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "answer_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "answer_id"})
)
public class AnswerReport extends BaseReport {

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
