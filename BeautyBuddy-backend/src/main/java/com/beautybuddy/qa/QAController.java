package com.beautybuddy.qa;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestParam;

import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.upvote.UpvoteService;
import com.beautybuddy.qa.dto.EditAnswerDTO;
import com.beautybuddy.qa.dto.EditQuestionDTO;
import com.beautybuddy.qa.dto.DisplayQuestionWithAnswersDTO;
import com.beautybuddy.qa.dto.SubmitAnswerDTO;
import com.beautybuddy.qa.dto.SubmitQuestionDTO;
import com.beautybuddy.report.ReportService;
import com.beautybuddy.report.dto.SubmitReportDTO;

@RestController
@RequestMapping("/api")
public class QAController {
    private final QAService qaService;
    private final UpvoteService upvoteService;
    private final ReportService reportService;

    public QAController(QAService qaService, UpvoteService upvoteService, ReportService reportService) {
        this.qaService = qaService;
        this.upvoteService = upvoteService;
        this.reportService = reportService;
    }

    @PostMapping("/questions/ask")
    public ResponseEntity<Void> askQuestion(@RequestBody SubmitQuestionDTO questionDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.addQuestion(userDetails.getEmail(), questionDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/edit")
    public ResponseEntity<Void> editQuestion(@RequestBody EditQuestionDTO questionDTO, @PathVariable Long questionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.editQuestion(userDetails.getEmail(), questionDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/questions/{questionId}")
    public ResponseEntity<Void> removeQuestion(@PathVariable Long questionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.removeQuestion(userDetails.getEmail(), questionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/upvote")
    public ResponseEntity<Void> upvoteQuestion(@PathVariable Long questionId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        upvoteService.upvote(userDetails.getEmail(), "question", questionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/remove-upvote")
    public ResponseEntity<Void> removeQuestionUpvote(@PathVariable Long targetId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        upvoteService.removeUpvote(userDetails.getEmail(), "question", targetId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/questions/{questionId}/report")
    public ResponseEntity<Void> reportQuestion(@RequestBody SubmitReportDTO reportDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reportService.report(userDetails.getEmail(), reportDTO.reason(), "question", reportDTO.targetId());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/submit")
    public ResponseEntity<Void> submitAnswer(@RequestBody SubmitAnswerDTO answerDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.addAnswer(userDetails.getEmail(), answerDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/edit")
    public ResponseEntity<Void> editAnswer(@RequestBody EditAnswerDTO answerDTO, @PathVariable Long answerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.editAnswer(userDetails.getEmail(), answerDTO);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/answers/{answerId}")
    public ResponseEntity<Void> removeAnswer(@PathVariable Long answerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        qaService.removeAnswer(userDetails.getEmail(), answerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/upvote")
    public ResponseEntity<Void> upvoteAnswer(@PathVariable Long answerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        upvoteService.upvote(userDetails.getEmail(), "answer", answerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/remove-upvote")
    public ResponseEntity<Void> removeAnswerUpvote(@PathVariable Long answerId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        upvoteService.removeUpvote(userDetails.getEmail(), "answer", answerId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/answers/{answerId}/report")
    public ResponseEntity<Void> reportAnswer(@RequestBody SubmitReportDTO reportDTO, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        reportService.report(userDetails.getEmail(), reportDTO.reason(), "answer", reportDTO.targetId());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/questions/{productId}")
    public ResponseEntity<Page<DisplayQuestionWithAnswersDTO>> getQuestionsForProduct(
        @PathVariable Long productId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        Authentication authentication
    ) {
        String email = null;
        if (authentication != null && authentication.isAuthenticated()) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            email = userDetails.getEmail();
        }
        return ResponseEntity.ok(qaService.getQuestionsAndAnswersForProduct(productId, page, size, email));
    }
}
