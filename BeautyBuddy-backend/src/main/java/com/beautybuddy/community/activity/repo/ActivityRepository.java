package com.beautybuddy.community.activity.repo;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.beautybuddy.community.activity.entity.Activity;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {

    Page<Activity> findByActor_Username(String username, Pageable pageable);

    Page<Activity> findByActor_UsernameIn(List<String> usernames, Pageable pageable);
}
