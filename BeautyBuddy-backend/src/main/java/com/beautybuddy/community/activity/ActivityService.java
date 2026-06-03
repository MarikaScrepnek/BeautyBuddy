package com.beautybuddy.community.activity;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.beautybuddy.community.activity.entity.Activity;
import com.beautybuddy.community.activity.entity.ActivityType;
import com.beautybuddy.community.activity.repo.ActivityRepository;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.user.entity.UserFollow;
import com.beautybuddy.user.repo.FollowRepository;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final FollowRepository followRepository;

    public ActivityService(ActivityRepository activityRepository, FollowRepository followRepository) {
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
    }

    public ResponseEntity<Activity> createActivity(User actor, ActivityType type, String payload) {
        Activity activity = new Activity();
        activity.setActor(actor);
        activity.setType(type);
        activity.setPayload(payload);
        Activity savedActivity = activityRepository.save(activity);
        return ResponseEntity.ok(savedActivity);
    }

    public Page<ResponseEntity<Activity>> getActivitiesByUsername(String username) {
        Page<Activity> activities = activityRepository.findByActor_Username(username, PageRequest.of(0, 10));
        return activities.map(ResponseEntity::ok);
    }

    public Page<ResponseEntity<Activity>> getFollowingActivitiesByUsername(String username) {
        Page<UserFollow> following = followRepository.findByFollowerUsername(username, PageRequest.of(0, 1000));
        List<String> followedUsernames = following.getContent().stream()
                .map(f -> f.getFollowed().getUsername()).collect(Collectors.toList());

        if (followedUsernames.isEmpty()) {
            return Page.<ResponseEntity<Activity>>empty(PageRequest.of(0, 10));
        }

        Page<Activity> activities = activityRepository.findByActor_UsernameIn(followedUsernames,
                PageRequest.of(0, 10));
        return activities.map(ResponseEntity::ok);
    }
}
