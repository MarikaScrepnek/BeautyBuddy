package com.beautybuddy.discussion.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.discussion.entity.DiscussionComment;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    
}
