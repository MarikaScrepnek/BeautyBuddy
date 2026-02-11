package com.beautybuddy.notification;

import com.beautybuddy.common.entity.ForeignKeyIdEntity;
import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account_notification_preference")
public class AccountNotificationPreference extends ForeignKeyIdEntity {
    @Id
    @OneToOne
    @JoinColumn(name = "account_id")
    private User user;

    @Column(name = "question_on_routine_product", nullable = false)
    private boolean questionOnRoutineProduct = true;

    @Column(name = "answer_on_your_question", nullable = false)
    private boolean answerOnYourQuestion = true;

    @Column(name = "discussion_comment_on_your_discussion", nullable = false)
    private boolean discussionCommentOnYourDiscussion = true;

    @Column(name = "discussion_comment_on_your_discussion_comment", nullable = false)
    private boolean discussionCommentOnYourDiscussionComment = true;

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

    public boolean isDiscussionCommentOnYourDiscussion() {
        return discussionCommentOnYourDiscussion;
    }
    public void setDiscussionCommentOnYourDiscussion(boolean discussionCommentOnYourDiscussion) {
        this.discussionCommentOnYourDiscussion = discussionCommentOnYourDiscussion;
    }

    public boolean isDiscussionCommentOnYourDiscussionComment() {
        return discussionCommentOnYourDiscussionComment;
    }
    public void setDiscussionCommentOnYourDiscussionComment(boolean discussionCommentOnYourDiscussionComment) {
        this.discussionCommentOnYourDiscussionComment = discussionCommentOnYourDiscussionComment;
    }

    public boolean isUpvotes() {
        return upvotes;
    }
    public void setUpvotes(boolean upvotes) {
        this.upvotes = upvotes;
    }

    @Override
    protected Long getForeignKeyId() {
        return user.getId();
    }
}
