package com.beautybuddy.upvote;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.config.RedisCacheConfig;
import com.beautybuddy.upvote.entity.AnswerUpvote;
import com.beautybuddy.upvote.entity.DiscussionCommentUpvote;
import com.beautybuddy.upvote.entity.DiscussionUpvote;
import com.beautybuddy.upvote.entity.QuestionUpvote;
import com.beautybuddy.upvote.entity.ReviewUpvote;
import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.discussion.repo.DiscussionCommentRepository;
import com.beautybuddy.discussion.repo.DiscussionRepository;
import com.beautybuddy.qa.Answer;
import com.beautybuddy.qa.AnswerRepository;
import com.beautybuddy.qa.Question;
import com.beautybuddy.qa.QuestionRepository;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.upvote.repo.AnswerUpvoteRepository;
import com.beautybuddy.upvote.repo.DiscussionCommentUpvoteRepository;
import com.beautybuddy.upvote.repo.DiscussionUpvoteRepository;
import com.beautybuddy.upvote.repo.QuestionUpvoteRepository;
import com.beautybuddy.upvote.repo.ReviewUpvoteRepository;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class UpvoteService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ReviewUpvoteRepository reviewUpvoteRepository;
    private final QuestionRepository questionRepository;
    private final QuestionUpvoteRepository questionUpvoteRepository;
    private final AnswerRepository answerRepository;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionUpvoteRepository discussionUpvoteRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final DiscussionCommentUpvoteRepository discussionCommentUpvoteRepository;

    private final Counter reviewUpvoteCounter;
    private final Counter questionUpvoteCounter;
    private final Counter answerUpvoteCounter;
    private final Counter discussionUpvoteCounter;
    private final Counter discussionCommentUpvoteCounter;

    public UpvoteService(ReviewRepository reviewRepository, UserRepository userRepository, ReviewUpvoteRepository reviewUpvoteRepository, QuestionRepository questionRepository, QuestionUpvoteRepository questionUpvoteRepository, AnswerRepository answerRepository, AnswerUpvoteRepository answerUpvoteRepository, DiscussionRepository discussionRepository, DiscussionUpvoteRepository discussionUpvoteRepository, DiscussionCommentRepository discussionCommentRepository, DiscussionCommentUpvoteRepository discussionCommentUpvoteRepository, MeterRegistry meterRegistry) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.reviewUpvoteRepository = reviewUpvoteRepository;
        this.questionRepository = questionRepository;
        this.questionUpvoteRepository = questionUpvoteRepository;
        this.answerRepository = answerRepository;
        this.answerUpvoteRepository = answerUpvoteRepository;
        this.discussionRepository = discussionRepository;
        this.discussionUpvoteRepository = discussionUpvoteRepository;
        this.discussionCommentRepository = discussionCommentRepository;
        this.discussionCommentUpvoteRepository = discussionCommentUpvoteRepository;
        this.reviewUpvoteCounter = Counter.builder("reviews_upvoted_total")
            .description("Total number of reviews upvoted")
            .register(meterRegistry);
        this.questionUpvoteCounter = Counter.builder("questions_upvoted_total")
            .description("Total number of questions upvoted")
            .register(meterRegistry);
        this.answerUpvoteCounter = Counter.builder("answers_upvoted_total")
            .description("Total number of answers upvoted")
            .register(meterRegistry);
        this.discussionUpvoteCounter = Counter.builder("discussions_upvoted_total")
            .description("Total number of discussions upvoted")
            .register(meterRegistry);
        this.discussionCommentUpvoteCounter = Counter.builder("discussion_comments_upvoted_total")
            .description("Total number of discussion comments upvoted")
            .register(meterRegistry);
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = RedisCacheConfig.DISCUSSION_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.DISCUSSION_SEARCH_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.QA_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.QA_SEARCH_FEED_CACHE, allEntries = true)
    })
    public void upvote(String email, String targetType, Long targetId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (targetType.equals("review")) {
            Review review = reviewRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
            if (reviewUpvoteRepository.findByUserAndReview(user, review).isPresent()) {
                return;
            }
        
            ReviewUpvote newUpvote = new ReviewUpvote();
            newUpvote.setUser(user);
            newUpvote.setReview(review);

            reviewUpvoteRepository.save(newUpvote);
            reviewUpvoteCounter.increment();
        }
        else if (targetType.equals("question")) {
            Question question = questionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
            if (questionUpvoteRepository.findByUserAndQuestion(user, question).isPresent()) {
                return;
            }
        
            QuestionUpvote newUpvote = new QuestionUpvote();
            newUpvote.setUser(user);
            newUpvote.setQuestion(question);

            questionUpvoteRepository.save(newUpvote);
            questionUpvoteCounter.increment();
        }
        else if (targetType.equals("answer")) {
            Answer answer = answerRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
            if (answerUpvoteRepository.findByUserAndAnswer(user, answer).isPresent()) {
                return;
            }
            AnswerUpvote newUpvote = new AnswerUpvote();
            newUpvote.setUser(user);
            newUpvote.setAnswer(answer);

            answerUpvoteRepository.save(newUpvote);
            answerUpvoteCounter.increment();
        }
        else if (targetType.equals("discussion")) {
            Discussion discussion = discussionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
            if (discussionUpvoteRepository.findByUserAndDiscussion(user, discussion).isPresent()) {
                return;
            }

            DiscussionUpvote newUpvote = new DiscussionUpvote();
            newUpvote.setUser(user);
            newUpvote.setDiscussion(discussion);

            discussionUpvoteRepository.save(newUpvote);
            discussionUpvoteCounter.increment();
        }
        else if (targetType.equals("discussion_comment")) {
            DiscussionComment comment = discussionCommentRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion comment not found"));
            if (discussionCommentUpvoteRepository.findByUserAndDiscussionComment(user, comment).isPresent()) {
                return;
            }

            DiscussionCommentUpvote newUpvote = new DiscussionCommentUpvote();
            newUpvote.setUser(user);
            newUpvote.setDiscussionComment(comment);

            discussionCommentUpvoteRepository.save(newUpvote);
            discussionCommentUpvoteCounter.increment();
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }

    @Transactional
    @Caching(evict = {
        @CacheEvict(cacheNames = RedisCacheConfig.DISCUSSION_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.DISCUSSION_SEARCH_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.REVIEW_SEARCH_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.QA_FEED_CACHE, allEntries = true),
        @CacheEvict(cacheNames = RedisCacheConfig.QA_SEARCH_FEED_CACHE, allEntries = true)
    })
    public void removeUpvote(String email, String targetType, Long targetId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (targetType.equals("review")) {
            Review review = reviewRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        
            ReviewUpvote existingUpvote = reviewUpvoteRepository.findByUserAndReview(user, review)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));

            reviewUpvoteRepository.delete(existingUpvote);
        }
        else if (targetType.equals("question")) {
           Question question = questionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
        
            QuestionUpvote existingUpvote = questionUpvoteRepository.findByUserAndQuestion(user, question)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));

            questionUpvoteRepository.delete(existingUpvote);
        }
        else if (targetType.equals("answer")) {
            Answer answer = answerRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
            AnswerUpvote existingUpvote = answerUpvoteRepository.findByUserAndAnswer(user, answer)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));
            
            answerUpvoteRepository.delete(existingUpvote);
        }
        else if (targetType.equals("discussion")) {
            Discussion discussion = discussionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
            DiscussionUpvote existingUpvote = discussionUpvoteRepository.findByUserAndDiscussion(user, discussion)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));
            
            discussionUpvoteRepository.delete(existingUpvote);
        }
        else if (targetType.equals("discussion_comment")) {
            DiscussionComment comment = discussionCommentRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion comment not found"));
            DiscussionCommentUpvote existingUpvote = discussionCommentUpvoteRepository.findByUserAndDiscussionComment(user, comment)
                .orElseThrow(() -> new RuntimeException("Upvote not found"));

            discussionCommentUpvoteRepository.delete(existingUpvote);
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }
}
