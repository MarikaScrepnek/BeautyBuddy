package com.beautybuddy.upvote.repo;

import java.util.Optional;

import com.beautybuddy.qa.Question;
import com.beautybuddy.upvote.BaseUpvoteRepository;
import com.beautybuddy.upvote.entity.QuestionUpvote;
import com.beautybuddy.user.entity.User;

public interface QuestionUpvoteRepository extends BaseUpvoteRepository<QuestionUpvote, Integer> {
	Optional<QuestionUpvote> findByUserAndQuestion(User user, Question question);
}
