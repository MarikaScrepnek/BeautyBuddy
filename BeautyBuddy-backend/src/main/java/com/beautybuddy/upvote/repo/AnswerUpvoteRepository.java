package com.beautybuddy.upvote.repo;

import java.util.Optional;

import com.beautybuddy.qa.Answer;
import com.beautybuddy.upvote.BaseUpvoteRepository;
import com.beautybuddy.upvote.entity.AnswerUpvote;
import com.beautybuddy.user.entity.User;

public interface AnswerUpvoteRepository extends BaseUpvoteRepository<AnswerUpvote, Integer> {
	Optional<AnswerUpvote> findByUserAndAnswer(User user, Answer answer);
}
