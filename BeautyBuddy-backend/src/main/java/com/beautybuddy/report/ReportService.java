package com.beautybuddy.report;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.config.RedisCacheConfig;
import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.discussion.repo.DiscussionCommentRepository;
import com.beautybuddy.discussion.repo.DiscussionRepository;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.repo.ProductRepository;
import com.beautybuddy.qa.Answer;
import com.beautybuddy.qa.AnswerRepository;
import com.beautybuddy.qa.Question;
import com.beautybuddy.qa.QuestionRepository;
import com.beautybuddy.report.entity.AnswerReport;
import com.beautybuddy.report.entity.DiscussionCommentReport;
import com.beautybuddy.report.entity.DiscussionReport;
import com.beautybuddy.report.entity.ProductReport;
import com.beautybuddy.report.entity.QuestionReport;
import com.beautybuddy.report.entity.ReviewReport;
import com.beautybuddy.report.repo.AnswerReportRepository;
import com.beautybuddy.report.repo.DiscussionCommentReportRepository;
import com.beautybuddy.report.repo.DiscussionReportRepository;
import com.beautybuddy.report.repo.ProductReportRepository;
import com.beautybuddy.report.repo.QuestionReportRepository;
import com.beautybuddy.report.repo.ReviewReportRepository;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;

@Service
public class ReportService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final ProductRepository productRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final QuestionReportRepository questionReportRepository;
    private final AnswerReportRepository answerReportRepository;
    private final DiscussionReportRepository discussionReportRepository;
    private final DiscussionCommentReportRepository discussionCommentReportRepository;
    private final ProductReportRepository productReportRepository;

    private final Counter reviewReportCounter;
    private final Counter questionReportCounter;
    private final Counter answerReportCounter;
    private final Counter discussionReportCounter;
    private final Counter discussionCommentReportCounter;
    private final Counter productReportCounter;

    public ReportService(UserRepository userRepository, ReviewRepository reviewRepository,
                         QuestionRepository questionRepository, AnswerRepository answerRepository,
                         DiscussionRepository discussionRepository, DiscussionCommentRepository discussionCommentRepository,
                         ProductRepository productRepository, ReviewReportRepository reviewReportRepository, QuestionReportRepository questionReportRepository,
                         AnswerReportRepository answerReportRepository, DiscussionReportRepository discussionReportRepository,
                         DiscussionCommentReportRepository discussionCommentReportRepository, ProductReportRepository productReportRepository, MeterRegistry meterRegistry) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.discussionRepository = discussionRepository;
        this.productRepository = productRepository;
        this.discussionCommentRepository = discussionCommentRepository;
        this.productReportRepository = productReportRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.questionReportRepository = questionReportRepository;
        this.answerReportRepository = answerReportRepository;
        this.discussionReportRepository = discussionReportRepository;
        this.discussionCommentReportRepository = discussionCommentReportRepository;
        this.reviewReportCounter = Counter.builder("reviews_reported_total")
            .description("Total number of reviews reported")
            .register(meterRegistry);
        this.questionReportCounter = Counter.builder("questions_reported_total")
            .description("Total number of questions reported")
            .register(meterRegistry);
        this.answerReportCounter = Counter.builder("answers_reported_total")
            .description("Total number of answers reported")
            .register(meterRegistry);
        this.discussionReportCounter = Counter.builder("discussions_reported_total")
            .description("Total number of discussions reported")
            .register(meterRegistry);
        this.discussionCommentReportCounter = Counter.builder("discussion_comments_reported_total")
            .description("Total number of discussion comments reported")
            .register(meterRegistry);
        this.productReportCounter = Counter.builder("products_reported_total")
            .description("Total number of products reported")
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
    public void report(String email, String reason, String targetType, Long targetId) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (targetType.equals("review")) {
            Review review = reviewRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Review not found"));
            if (reviewReportRepository.findByUserAndReview(user, review).isPresent()) {
                return;
            }
        
            ReviewReport newReport = new ReviewReport();
            newReport.setUser(user);
            newReport.setReview(review);
            newReport.setReason(reason);

            reviewReportRepository.save(newReport);
            reviewReportCounter.increment();
        }
        else if (targetType.equals("question")) {
            Question question = questionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
            if (questionReportRepository.findByUserAndQuestion(user, question).isPresent()) {
                return;
            }
            QuestionReport newReport = new QuestionReport();
            newReport.setUser(user);
            newReport.setQuestion(question);
            newReport.setReason(reason);

            questionReportRepository.save(newReport);
            questionReportCounter.increment();
        }
        else if (targetType.equals("answer")) {
            Answer answer = answerRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Answer not found"));
            if (answerReportRepository.findByUserAndAnswer(user, answer).isPresent()) {
                return;
            }
            AnswerReport newReport = new AnswerReport();
            newReport.setUser(user);
            newReport.setAnswer(answer);
            newReport.setReason(reason);

            answerReportRepository.save(newReport);
            answerReportCounter.increment();
        }
        else if (targetType.equals("discussion")) {
            Discussion discussion = discussionRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion not found"));
            if (discussionReportRepository.findByUserAndDiscussion(user, discussion).isPresent()) {
                return;
            }
            DiscussionReport newReport = new DiscussionReport();
            newReport.setUser(user);
            newReport.setDiscussion(discussion);
            newReport.setReason(reason);

            discussionReportRepository.save(newReport);
            discussionReportCounter.increment();
        }
        else if (targetType.equals("discussion_comment")) {
            DiscussionComment discussionComment = discussionCommentRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Discussion comment not found"));
            if (discussionCommentReportRepository.findByUserAndDiscussionComment(user, discussionComment).isPresent()) {
                return;
            }
            DiscussionCommentReport newReport = new DiscussionCommentReport();
            newReport.setUser(user);
            newReport.setDiscussionComment(discussionComment);
            newReport.setReason(reason);

            discussionCommentReportRepository.save(newReport);
            discussionCommentReportCounter.increment();
        }
        else if (targetType.equals("product")) {
            Product product = productRepository.findById(targetId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
            if (productReportRepository.findByUserAndProduct(user, product).isPresent()) {
                return;
            }
            ProductReport newReport = new ProductReport();
            newReport.setUser(user);
            newReport.setProduct(product);
            newReport.setReason(reason);

            productReportRepository.save(newReport);
            productReportCounter.increment();
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }
}
