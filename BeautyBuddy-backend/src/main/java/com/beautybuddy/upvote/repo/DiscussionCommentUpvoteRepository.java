package com.beautybuddy.upvote.repo;

import java.util.Optional;

import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.upvote.BaseUpvoteRepository;
import com.beautybuddy.upvote.entity.DiscussionCommentUpvote;
import com.beautybuddy.user.entity.User;

public interface DiscussionCommentUpvoteRepository extends BaseUpvoteRepository<DiscussionCommentUpvote, Integer> {
    Optional<DiscussionCommentUpvote> findByUserAndDiscussionComment(User user, DiscussionComment discussionComment);
}
