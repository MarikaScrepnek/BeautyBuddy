package com.beautybuddy.qa;

import com.beautybuddy.qa.dto.EditAnswerDTO;
import com.beautybuddy.qa.dto.EditQuestionDTO;
import com.beautybuddy.qa.dto.SubmitAnswerDTO;
import com.beautybuddy.qa.dto.SubmitQuestionDTO;
import com.beautybuddy.user.User;
import com.beautybuddy.product.Product;
import com.beautybuddy.user.UserRepository;
import com.beautybuddy.product.ProductRepository;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QAService {
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    public QAService(UserRepository userRepository, ProductRepository productRepository,
                     QuestionRepository questionRepository, AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
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
        if (oldQuestion.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(15))) {
            throw new RuntimeException("Cannot edit a question older than 15 minutes");
        }
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        if (!oldQuestion.getUser().equals(user)) {
            throw new RuntimeException("User is not authorized to edit this question");
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
        if (!question.getUser().equals(user)) {
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
        if (!oldAnswer.getUser().equals(user)) {
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
        if (!answer.getUser().equals(user)) {
            throw new RuntimeException("User is not authorized to delete this answer");
        }
        answer.setDeletedAt(LocalDateTime.now());
        answerRepository.save(answer);
    }

    //implement GETMAPPING for questions and answers
}
