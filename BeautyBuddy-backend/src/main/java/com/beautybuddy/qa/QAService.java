package com.beautybuddy.qa;

import com.beautybuddy.qa.dto.EditAnswerDTO;
import com.beautybuddy.qa.dto.EditQuestionDTO;
import com.beautybuddy.qa.dto.SubmitAnswerDTO;
import com.beautybuddy.qa.dto.SubmitQuestionDTO;
import com.beautybuddy.qa.dto.DisplayAnswerDTO;
import com.beautybuddy.qa.dto.DisplayQuestionWithAnswersDTO;
import com.beautybuddy.report.repo.AnswerReportRepository;
import com.beautybuddy.report.repo.QuestionReportRepository;
import com.beautybuddy.upvote.repo.AnswerUpvoteRepository;
import com.beautybuddy.upvote.repo.QuestionUpvoteRepository;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.product.entity.Product;
import com.beautybuddy.product.repo.ProductRepository;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Service
public class QAService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final QuestionUpvoteRepository questionUpvoteRepository;
    private final AnswerUpvoteRepository answerUpvoteRepository;
    private final QuestionReportRepository questionReportRepository;
    private final AnswerReportRepository answerReportRepository;
    public QAService(UserRepository userRepository, ProductRepository productRepository,
                     QuestionRepository questionRepository, AnswerRepository answerRepository,
                     QuestionUpvoteRepository questionUpvoteRepository, AnswerUpvoteRepository answerUpvoteRepository,
                     QuestionReportRepository questionReportRepository, AnswerReportRepository answerReportRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.questionUpvoteRepository = questionUpvoteRepository;
        this.answerUpvoteRepository = answerUpvoteRepository;
        this.questionReportRepository = questionReportRepository;
        this.answerReportRepository = answerReportRepository;
    }

    @Transactional
    public void addQuestion(String email, SubmitQuestionDTO question) {
        Question newQuestion = new Question();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Product product = productRepository.findById(question.productId())
            .orElseThrow(() -> new RuntimeException("Product not found"));

        newQuestion.setUser(user);
        newQuestion.setProduct(product);
        newQuestion.setText(question.text());
        questionRepository.save(newQuestion);
    }

    @Transactional
    public void editQuestion(String email, EditQuestionDTO question) {
        Question oldQuestion = questionRepository.findById(question.questionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));

        if (!oldQuestion.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to edit this question");
        }

        if (oldQuestion.isAnswered()) {
            throw new RuntimeException("Cannot edit question with answers");
        }

        oldQuestion.setText(question.text());
        questionRepository.save(oldQuestion);
    }

    @Transactional
    public void removeQuestion(String email, Long questionId) {
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!question.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to delete this question");
        }
        question.setDeletedAt(LocalDateTime.now());
        questionRepository.save(question);
    }



    @Transactional
    public void addAnswer(String email, SubmitAnswerDTO answer) {
        Answer newAnswer = new Answer();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Question question = questionRepository.findById(answer.questionId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        String answerText = answer.text();

        newAnswer.setUser(user);
        newAnswer.setQuestion(question);
        newAnswer.setText(answerText);
        question.getAnswers().add(newAnswer);
        questionRepository.save(question);
    }

    @Transactional
    public void editAnswer(String email, EditAnswerDTO answer) {
        Answer oldAnswer = answerRepository.findById(answer.answerId())
            .orElseThrow(() -> new RuntimeException("Answer not found"));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!oldAnswer.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to edit this answer");
        }
        oldAnswer.setText(answer.text());
        answerRepository.save(oldAnswer);
    }

    @Transactional
    public void removeAnswer(String email, Long answerId) {
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Answer not found"));
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!answer.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("User is not authorized to delete this answer");
        }
        answer.setDeletedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }

    @Transactional(readOnly = true)
    private PageRequest buildQuestionPageRequest(int page, int size, String sortKey) {
        String effectiveSortKey = sortKey == null ? "created_desc" : sortKey;
        Sort sort = switch (effectiveSortKey) {
            case "helpful_desc" -> Sort.by(Sort.Direction.DESC, "upvoteCount");
            case "created_asc" -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "created_desc" -> Sort.by(Sort.Direction.DESC, "createdAt");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
        return PageRequest.of(page, size, sort);
    }

    @Transactional(readOnly = true)
    public Page<DisplayQuestionWithAnswersDTO> getQuestionsAndAnswersForProduct(Long productId, int page, int size, String sortKey, String userEmail) {
        final User currentUser = userEmail != null
            ? userRepository.findByEmail(userEmail).orElse(null)
            : null;
        PageRequest pageRequest = buildQuestionPageRequest(page, size, sortKey);
        Page<Question> questions = questionRepository
            .findByProduct_IdAndDeletedAtIsNullAndApprovedTrue(
                productId,
                pageRequest
            );

        Set<Long> reportedQuestionIds = currentUser == null
            ? Set.of()
            : questionReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getQuestion().getId())
                .collect(Collectors.toSet());

        Set<Long> reportedAnswerIds = currentUser == null
            ? Set.of()
            : answerReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getAnswer().getId())
                .collect(Collectors.toSet());

        List<DisplayQuestionWithAnswersDTO> mapped = questions.getContent().stream()
            .filter(question -> !reportedQuestionIds.contains(question.getId()))
            .map(question -> {
            List<DisplayAnswerDTO> answers = question.getAnswers().stream()
                .filter(answer -> answer.getDeletedAt() == null && answer.isApproved())
                .filter(answer -> !reportedAnswerIds.contains(answer.getId()))
                .sorted(Comparator.comparing(Answer::getCreatedAt))
                .map(answer -> new DisplayAnswerDTO(
                    answer.getId(),
                    question.getId(),
                    answer.getText(),
                    answer.getUser().getUsername(),
                    answer.getCreatedAt(),
                    answer.getUpvoteCount(),
                    currentUser != null
                        && answerUpvoteRepository.findByUserAndAnswer(currentUser, answer).isPresent()
                ))
                .toList();

            return new DisplayQuestionWithAnswersDTO(
                question.getId(),
                question.getProduct().getId(),
                question.getText(),
                question.getUser().getUsername(),
                question.getCreatedAt(),
                question.getUpvoteCount(),
                currentUser != null
                    && questionUpvoteRepository.findByUserAndQuestion(currentUser, question).isPresent(),
                answers
            );
        })
            .toList();

        return new PageImpl<>(mapped, questions.getPageable(), mapped.size());
    }

    public Page<DisplayQuestionWithAnswersDTO> searchQuestionsAndAnswersForProduct(Long productId, String query, int page, int size, String sortKey, String userEmail) {
        final User currentUser = userEmail != null
            ? userRepository.findByEmail(userEmail).orElse(null)
            : null;
        PageRequest pageRequest = buildQuestionPageRequest(page, size, sortKey);
        Page<Question> questions = questionRepository
            .searchQuestionsByProductAndQuery(
                productId,
                query,
                pageRequest
            );

        Set<Long> reportedQuestionIds = currentUser == null
            ? Set.of()
            : questionReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getQuestion().getId())
                .collect(Collectors.toSet());

        Set<Long> reportedAnswerIds = currentUser == null
            ? Set.of()
            : answerReportRepository.findAllByUser(currentUser).stream()
                .map(report -> report.getAnswer().getId())
                .collect(Collectors.toSet());

        List<DisplayQuestionWithAnswersDTO> mapped = questions.getContent().stream()
            .filter(question -> !reportedQuestionIds.contains(question.getId()))
            .map(question -> {
                List<DisplayAnswerDTO> answers = question.getAnswers().stream()
                    .filter(answer -> answer.getDeletedAt() == null && answer.isApproved())
                    .filter(answer -> !reportedAnswerIds.contains(answer.getId()))
                    .sorted(Comparator.comparing(Answer::getCreatedAt))
                    .map(answer -> new DisplayAnswerDTO(
                        answer.getId(),
                        question.getId(),
                        answer.getText(),
                        answer.getUser().getUsername(),
                        answer.getCreatedAt(),
                        answer.getUpvoteCount(),
                        currentUser != null
                            && answerUpvoteRepository.findByUserAndAnswer(currentUser, answer).isPresent()
                    ))
                    .toList();

                return new DisplayQuestionWithAnswersDTO(
                    question.getId(),
                    question.getProduct().getId(),
                    question.getText(),
                    question.getUser().getUsername(),
                    question.getCreatedAt(),
                    question.getUpvoteCount(),
                    currentUser != null
                        && questionUpvoteRepository.findByUserAndQuestion(currentUser, question).isPresent(),
                    answers
                );
            })
            .toList();

        return new PageImpl<>(mapped, questions.getPageable(), mapped.size());
    }
}
