package com.beautybuddy.qa;

import com.beautybuddy.common.entity.UserWrittenEntity;
import com.beautybuddy.product.entity.Product;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.HashSet;
import java.util.Set;

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


    
    @OneToMany(
        mappedBy = "question",
        cascade = jakarta.persistence.CascadeType.ALL,
        orphanRemoval = true
    )
    private Set<Answer> answers = new HashSet<>();

    public Set<Answer> getAnswers() {
        return answers;
    }
    public void setAnswers(Set<Answer> answers) {
        this.answers = answers;
    }
}
