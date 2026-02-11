package com.beautybuddy.qa;

import com.beautybuddy.common.entity.UserWrittenEntity;
import com.beautybuddy.product.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "question"
)
public class Question extends UserWrittenEntity {

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "is_answered", nullable = false)
    private boolean isAnswered = false;

    @Column(name = "answer_count", nullable = false)
    private int answerCount = 0;

    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }

    public boolean isAnswered() {
        return isAnswered;
    }
    public void setAnswered(boolean isAnswered) {
        this.isAnswered = isAnswered;
    }

    public int getAnswerCount() {
        return answerCount;
    }
    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }
}
