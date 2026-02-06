package com.beautybuddy.report.entity;

import com.beautybuddy.report.BaseReport;
import com.beautybuddy.qa.Question;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(
    name = "question_report",
    uniqueConstraints = @UniqueConstraint(columnNames = {"account_id", "question_id"})
)
public class QuestionReport extends BaseReport {

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
