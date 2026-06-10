package com.beautybuddy.community.activity;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.community.activity.entity.Activity;
import com.beautybuddy.security.CustomUserDetails;

@RestController
@RequestMapping("/api/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{username}")
    public Page<ResponseEntity<Activity>> getActivities(@PathVariable String username) {
        return activityService.getActivitiesByUsername(username);
    }

    @GetMapping("/following")
    public Page<ResponseEntity<Activity>> getFollowingActivities(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return Page.empty();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        return activityService.getFollowingActivitiesByUsername(userDetails.getUsername());
    }
}
