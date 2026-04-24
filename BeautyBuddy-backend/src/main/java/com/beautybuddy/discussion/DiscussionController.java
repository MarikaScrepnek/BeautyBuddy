package com.beautybuddy.discussion;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.discussion.dto.AddDiscussionCommentDTO;
import com.beautybuddy.discussion.dto.AddDiscussionDTO;
import com.beautybuddy.discussion.dto.DisplayDiscussionDTO;
import com.beautybuddy.report.ReportRequestDTO;
import com.beautybuddy.report.ReportService;
import com.beautybuddy.security.CustomUserDetails;
import com.beautybuddy.upvote.UpvoteService;

@RestController
@RequestMapping("/api/discussions")
public class DiscussionController {
    private final DiscussionService discussionService;
    private final UpvoteService upvoteService;
    private final ReportService reportService;

    private CustomUserDetails getUserDetails(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUserDetails userDetails) {
            return userDetails;
        }
        return null;
    }

    public DiscussionController(DiscussionService discussionService, UpvoteService upvoteService, ReportService reportService) {
        this.discussionService = discussionService;
        this.upvoteService = upvoteService;
        this.reportService = reportService;
    }

    @GetMapping
    public ResponseEntity<Page<DisplayDiscussionDTO>> getDiscussions(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        Authentication authentication
    ) {
        String userEmail = null;
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails != null) {
            userEmail = userDetails.getEmail();
        }
        Page<DisplayDiscussionDTO> discussions = discussionService.getDiscussions(userEmail, page, size, sort);
        return ResponseEntity.ok(discussions);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DisplayDiscussionDTO>> searchDiscussions(
        @RequestParam String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(required = false) String sort,
        Authentication authentication
    ) {
        String userEmail = null;
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails != null) {
            userEmail = userDetails.getEmail();
        }
        Page<DisplayDiscussionDTO> discussions = discussionService.searchDiscussions(userEmail, query, page, size, sort);
        return ResponseEntity.ok(discussions);
    }

    @PostMapping
    public ResponseEntity<Void> addDiscussion(@RequestBody AddDiscussionDTO discussionDTO, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        discussionService.addDiscussion(userDetails.getEmail(), discussionDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{discussionId}/comment")
        public ResponseEntity<Void> addComment(@PathVariable Long discussionId, @RequestBody AddDiscussionCommentDTO commentDTO, Authentication authentication) {
            CustomUserDetails userDetails = getUserDetails(authentication);
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            discussionService.addComment(userDetails.getEmail(), discussionId, commentDTO);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/{discussionId}/edit")
        public ResponseEntity<Void> editDiscussion(@PathVariable Long discussionId, @RequestBody AddDiscussionDTO updatedDiscussionDTO, Authentication authentication) {
            CustomUserDetails userDetails = getUserDetails(authentication);
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            discussionService.editDiscussion(userDetails.getEmail(), discussionId, updatedDiscussionDTO);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}/edit")
        public ResponseEntity<Void> editComment(@PathVariable Long commentId, @RequestBody AddDiscussionCommentDTO commentDTO, Authentication authentication) {
            CustomUserDetails userDetails = getUserDetails(authentication);
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            discussionService.editComment(userDetails.getEmail(), commentId, commentDTO.text());
            return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{discussionId}")
        public ResponseEntity<Void> deleteDiscussion(@PathVariable Long discussionId, Authentication authentication) {
            CustomUserDetails userDetails = getUserDetails(authentication);
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            discussionService.deleteDiscussion(userDetails.getEmail(), discussionId);
            return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{discussionId}/comment/{commentId}")
        public ResponseEntity<Void> deleteComment(@PathVariable Long discussionId, @PathVariable Long commentId, Authentication authentication) {
            CustomUserDetails userDetails = getUserDetails(authentication);
            if (userDetails == null) {
                return ResponseEntity.status(401).build();
            }
            discussionService.deleteComment(userDetails.getEmail(), commentId);
            return ResponseEntity.ok().build();
    }

    @PostMapping("/{discussionId}/upvote")
    public ResponseEntity<Void> upvoteDiscussion(@PathVariable Long discussionId, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        upvoteService.upvote(userDetails.getEmail(), "discussion", discussionId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{discussionId}/upvote")
    public ResponseEntity<Void> removeUpvoteDiscussion(@PathVariable Long discussionId, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        upvoteService.removeUpvote(userDetails.getEmail(), "discussion", discussionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}/upvote")
    public ResponseEntity<Void> upvoteComment(@PathVariable Long commentId, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        upvoteService.upvote(userDetails.getEmail(), "discussion_comment", commentId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comments/{commentId}/upvote")
    public ResponseEntity<Void> removeUpvoteComment(@PathVariable Long commentId, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        upvoteService.removeUpvote(userDetails.getEmail(), "discussion_comment", commentId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{discussionId}/report")
    public ResponseEntity<Void> reportDiscussion(@PathVariable Long discussionId, @RequestBody ReportRequestDTO reportRequestDTO, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        reportService.report(userDetails.getEmail(), reportRequestDTO.reason(), "discussion", discussionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comments/{commentId}/report")
    public ResponseEntity<Void> reportComment(@PathVariable Long commentId, @RequestBody ReportRequestDTO reportRequestDTO, Authentication authentication) {
        CustomUserDetails userDetails = getUserDetails(authentication);
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        reportService.report(userDetails.getEmail(), reportRequestDTO.reason(), "discussion_comment", commentId);
        return ResponseEntity.ok().build();
    }

}