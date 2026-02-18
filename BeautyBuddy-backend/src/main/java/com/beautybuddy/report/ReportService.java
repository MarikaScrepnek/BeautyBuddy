package com.beautybuddy.report;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.beautybuddy.user.User;
import com.beautybuddy.discussion.Discussion;
import com.beautybuddy.discussion.DiscussionComment;
import com.beautybuddy.discussion.repo.DiscussionCommentRepository;
import com.beautybuddy.discussion.repo.DiscussionRepository;
import com.beautybuddy.qa.Answer;
import com.beautybuddy.qa.AnswerRepository;
import com.beautybuddy.qa.Question;
import com.beautybuddy.qa.QuestionRepository;
import com.beautybuddy.report.entity.AnswerReport;
import com.beautybuddy.report.entity.DiscussionCommentReport;
import com.beautybuddy.report.entity.DiscussionReport;
import com.beautybuddy.report.entity.QuestionReport;
import com.beautybuddy.report.entity.ReviewReport;
import com.beautybuddy.report.repo.AnswerReportRepository;
import com.beautybuddy.report.repo.DiscussionCommentReportRepository;
import com.beautybuddy.report.repo.DiscussionReportRepository;
import com.beautybuddy.report.repo.QuestionReportRepository;
import com.beautybuddy.report.repo.ReviewReportRepository;
import com.beautybuddy.review.ReviewRepository;
import com.beautybuddy.review.entity.Review;
import com.beautybuddy.user.UserRepository;

@Service
public class ReportService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;
    private final ReviewReportRepository reviewReportRepository;
    private final QuestionReportRepository questionReportRepository;
    private final AnswerReportRepository answerReportRepository;
    private final DiscussionReportRepository discussionReportRepository;
    private final DiscussionCommentReportRepository discussionCommentReportRepository;
    
    public ReportService(UserRepository userRepository, ReviewRepository reviewRepository,
                         QuestionRepository questionRepository, AnswerRepository answerRepository,
                         DiscussionRepository discussionRepository, DiscussionCommentRepository discussionCommentRepository,
                         ReviewReportRepository reviewReportRepository, QuestionReportRepository questionReportRepository,
                         AnswerReportRepository answerReportRepository, DiscussionReportRepository discussionReportRepository,
                         DiscussionCommentReportRepository discussionCommentReportRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.discussionRepository = discussionRepository;
        this.discussionCommentRepository = discussionCommentRepository;
        this.reviewReportRepository = reviewReportRepository;
        this.questionReportRepository = questionReportRepository;
        this.answerReportRepository = answerReportRepository;
        this.discussionReportRepository = discussionReportRepository;
        this.discussionCommentReportRepository = discussionCommentReportRepository;
    }

    @Transactional
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
        }
        else {
            throw new RuntimeException("Invalid target type");
        }
    }
}
