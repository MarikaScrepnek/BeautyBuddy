package com.beautybuddy.discussion;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.beautybuddy.discussion.dto.AddDiscussionCommentDTO;
import com.beautybuddy.discussion.dto.AddDiscussionDTO;
import com.beautybuddy.discussion.dto.DisplayDiscussionDTO;
import com.beautybuddy.discussion.dto.DisplayCommentDTO;
import com.beautybuddy.discussion.entity.Discussion;
import com.beautybuddy.discussion.entity.DiscussionComment;
import com.beautybuddy.discussion.repo.DiscussionCommentRepository;
import com.beautybuddy.discussion.repo.DiscussionRepository;
import com.beautybuddy.upvote.repo.DiscussionCommentUpvoteRepository;
import com.beautybuddy.upvote.repo.DiscussionUpvoteRepository;
import com.beautybuddy.user.User;
import com.beautybuddy.user.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class DiscussionService {
    private final UserRepository userRepository;
    private final DiscussionRepository discussionRepository;
    private final DiscussionCommentRepository discussionCommentRepository;

    private final DiscussionUpvoteRepository discussionUpvoteRepository;
    private final DiscussionCommentUpvoteRepository discussionCommentUpvoteRepository;

    public DiscussionService(UserRepository userRepository, DiscussionRepository discussionRepository, DiscussionCommentRepository discussionCommentRepository, DiscussionUpvoteRepository discussionUpvoteRepository, DiscussionCommentUpvoteRepository discussionCommentUpvoteRepository) {
        this.userRepository = userRepository;
        this.discussionRepository = discussionRepository;
        this.discussionCommentRepository = discussionCommentRepository;
        this.discussionUpvoteRepository = discussionUpvoteRepository;
        this.discussionCommentUpvoteRepository = discussionCommentUpvoteRepository;
    }

    @Transactional
    public void addDiscussion(String userEmail, AddDiscussionDTO discussionDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Discussion discussion = new Discussion();
        discussion.setTitle(discussionDTO.title());
        discussion.setText(discussionDTO.text());
        discussion.setUser(user);
        discussionRepository.save(discussion);
    }

    @Transactional
    public void editDiscussion(String userEmail, Long discussionId, AddDiscussionDTO updatedDiscussionDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Discussion discussion = discussionRepository.findById(discussionId)
            .orElseThrow(() -> new RuntimeException("Discussion not found"));
        if (!discussion.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (discussion.getReplyCount() > 0) {
            throw new RuntimeException("Cannot edit discussion with replies");
        }

        if (updatedDiscussionDTO.title() != null) {
             discussion.setTitle(updatedDiscussionDTO.title());
        }

        if (updatedDiscussionDTO.text() != null) {
            discussion.setText(updatedDiscussionDTO.text());
        }
        
        discussion.setUpdatedAt(LocalDateTime.now());
        discussionRepository.save(discussion);
    }

    @Transactional
    public void deleteDiscussion(String userEmail, Long discussionId) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Discussion discussion = discussionRepository.findById(discussionId)
            .orElseThrow(() -> new RuntimeException("Discussion not found"));
        if (!discussion.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        discussion.setDeletedAt(LocalDateTime.now());
        discussionRepository.save(discussion);
    }

    @Transactional
    public void addComment(String userEmail, Long discussionId, AddDiscussionCommentDTO commentDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        Discussion discussion = discussionRepository.findById(discussionId)
            .orElseThrow(() -> new RuntimeException("Discussion not found"));
        
        DiscussionComment comment = new DiscussionComment();
        comment.setDiscussion(discussion);
        comment.setText(commentDTO.text());
        comment.setUser(user);
        comment.setDiscussion(discussion);
        if (commentDTO.parentDiscussionCommentId() != null) {
            DiscussionComment parentComment = discussionCommentRepository.findById(commentDTO.parentDiscussionCommentId())
                .orElseThrow(() -> new RuntimeException("Parent comment not found"));
            comment.setParentDiscussionComment(parentComment);
        }
        discussionCommentRepository.save(comment);
    }

    @Transactional
    public void editComment(String userEmail, Long commentId, AddDiscussionCommentDTO commentDTO) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        DiscussionComment comment = discussionCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (comment.getReplyCount() > 0) {
            throw new RuntimeException("Cannot edit comment with replies");
        }

        comment.setText(commentDTO.text());
        comment.setUpdatedAt(LocalDateTime.now());
        discussionCommentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(String userEmail, Long commentId) {
        User user = userRepository.findByEmail(userEmail)
            .orElseThrow(() -> new RuntimeException("User not found"));
        DiscussionComment comment = discussionCommentRepository.findById(commentId)
            .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized");
        }
        comment.setDeletedAt(LocalDateTime.now());
        discussionCommentRepository.save(comment);
    }

    public Page<DisplayDiscussionDTO> getDiscussions(String userEmail, int page, int size) {
        User currentUser = userRepository.findByEmail(userEmail).orElse(null);
        return discussionRepository.findAll(PageRequest.of(page, size))
            .map(discussion -> new DisplayDiscussionDTO(
                discussion.getId(),
                discussion.getCreatedAt(),
                discussion.getTitle(),
                discussion.getText(),
                discussion.getUser().getUsername(),
                discussion.getUpvoteCount(),
                discussion.getComments().size(),
                discussion.getComments().stream()
                    .map(comment -> new DisplayCommentDTO(
                        comment.getParentDiscussionComment() != null ? comment.getParentDiscussionComment().getId() : null,
                        comment.getId(),
                        comment.getCreatedAt(),
                        comment.getText(),
                        comment.getUser().getUsername(),
                        comment.getUpvoteCount(),
                        comment.getReplyCount(),
                        discussionCommentUpvoteRepository.findByUserAndDiscussionComment(currentUser, comment).isPresent()
                    ))
                    .toList(),
                discussionUpvoteRepository.findByUserAndDiscussion(currentUser, discussion).isPresent()
            ));
    }

    public Page<DisplayDiscussionDTO> searchDiscussions(String userEmail, String query, int page, int size) {
        User currentUser = userRepository.findByEmail(userEmail).orElse(null);
        Page<Discussion> results = discussionRepository.findAll(PageRequest.of(page, size));
        List<DisplayDiscussionDTO> filtered = results.getContent().stream()
            .filter(discussion -> discussion.getTitle().toLowerCase().contains(query.toLowerCase()) || discussion.getText().toLowerCase().contains(query.toLowerCase()))
            .map(discussion -> new DisplayDiscussionDTO(
                discussion.getId(),
                discussion.getCreatedAt(),
                discussion.getTitle(),
                discussion.getText(),
                discussion.getUser().getUsername(),
                discussion.getUpvoteCount(),
                discussion.getComments().size(),
                discussion.getComments().stream()
                    .map(comment -> new DisplayCommentDTO(
                        comment.getParentDiscussionComment() != null ? comment.getParentDiscussionComment().getId() : null,
                        comment.getId(),
                        comment.getCreatedAt(),
                        comment.getText(),
                        comment.getUser().getUsername(),
                        comment.getUpvoteCount(),
                        comment.getReplyCount(),
                        discussionCommentUpvoteRepository.findByUserAndDiscussionComment(currentUser, comment).isPresent()
                    ))
                    .toList(),
                discussionUpvoteRepository.findByUserAndDiscussion(currentUser, discussion).isPresent()
                
            ))
            .toList();
        return new PageImpl<>(filtered, PageRequest.of(page, size), filtered.size());
    }
}
