package com.beautybuddy.upvote;

import org.springframework.data.jpa.repository.JpaRepository;

public interface BaseUpvoteRepository<T extends BaseUpvote, ID> extends JpaRepository<T, ID> {

}
