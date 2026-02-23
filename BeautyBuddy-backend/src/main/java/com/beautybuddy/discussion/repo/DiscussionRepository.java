package com.beautybuddy.discussion.repo;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beautybuddy.discussion.entity.Discussion;

public interface DiscussionRepository extends JpaRepository<Discussion, Long> {
    
}
