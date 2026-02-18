package com.beautybuddy.discussion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beautybuddy.discussion.DiscussionComment;

public interface DiscussionCommentRepository extends JpaRepository<DiscussionComment, Long> {
    
}
