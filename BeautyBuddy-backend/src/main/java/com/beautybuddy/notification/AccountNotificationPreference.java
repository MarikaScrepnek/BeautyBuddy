package com.beautybuddy.notification;

import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

public class AccountNotificationPreference {
    @Id
    @OneToOne
    @JoinColumn(name = "account_id")
    private User user;

    @Column(name = "question_on_routine_product", nullable = false)
    private boolean questionOnRoutineProduct = true;

    @Column(name = "answer_on_your_question", nullable = false)
    private boolean answerOnYourQuestion = true;

    @Column(name = "discussion_comment_on_your_comment", nullable = false)
    private boolean discussionCommentOnYourComment = true;

    @Column(name = "upvotes", nullable = false)
    private boolean upvotes = true;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public boolean isQuestionOnRoutineProduct() {
        return questionOnRoutineProduct;
    }
    public void setQuestionOnRoutineProduct(boolean questionOnRoutineProduct) {
        this.questionOnRoutineProduct = questionOnRoutineProduct;
    }

    public boolean isAnswerOnYourQuestion() {
        return answerOnYourQuestion;
    }
    public void setAnswerOnYourQuestion(boolean answerOnYourQuestion) {
        this.answerOnYourQuestion = answerOnYourQuestion;
    }

    public boolean isDiscussionCommentOnYourComment() {
        return discussionCommentOnYourComment;
    }
    public void setDiscussionCommentOnYourComment(boolean discussionCommentOnYourComment) {
        this.discussionCommentOnYourComment = discussionCommentOnYourComment;
    }

    public boolean isUpvotes() {
        return upvotes;
    }
    public void setUpvotes(boolean upvotes) {
        this.upvotes = upvotes;
    }
}
