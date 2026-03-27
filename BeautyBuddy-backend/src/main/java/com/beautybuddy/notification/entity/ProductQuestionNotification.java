package com.beautybuddy.notification.entity;

import com.beautybuddy.notification.BaseNotificationType;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.qa.Question;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table (
    name = "product_question_notification"
)
public class ProductQuestionNotification extends BaseNotificationType {
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public Question getQuestion() {
        return question;
    }
    public void setQuestion(Question question) {
        this.question = question;
    }
}
