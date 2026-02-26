package com.beautybuddy.upvote.repo;

import java.util.Optional;

import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.upvote.BaseUpvoteRepository;
import com.beautybuddy.upvote.entity.DiscussionUpvote;
import com.beautybuddy.user.entity.User;

public interface DiscussionUpvoteRepository extends BaseUpvoteRepository<DiscussionUpvote, Integer> {
    Optional<DiscussionUpvote> findByUserAndDiscussion(User user, Discussion discussion);
}
