package com.beautybuddy.community.activity;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.community.activity.entity.Activity;
import com.beautybuddy.community.activity.entity.ActivityAction;
import com.beautybuddy.community.activity.entity.ActivityType;
import com.beautybuddy.community.activity.repo.ActivityRepository;
import com.beautybuddy.community.dto.ActivityDTO;
import com.beautybuddy.user.entity.User;
import com.beautybuddy.user.entity.UserFollow;
import com.beautybuddy.user.repo.FollowRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ActivityService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final ActivityRepository activityRepository;
    private final FollowRepository followRepository;

    public ActivityService(ActivityRepository activityRepository, FollowRepository followRepository) {
        this.activityRepository = activityRepository;
        this.followRepository = followRepository;
    }

    public ResponseEntity<Void> createActivity(User actor, ActivityType type, Long targetId, String payload) {
        return createActivity(actor, type, targetId, payload, null, null, null, null, null);
    }

    public ResponseEntity<Void> createActivity(
            User actor,
            ActivityType type,
            Long targetId,
            String payload,
            Long productId,
            String productName,
            Long shadeId,
            String shadeName,
            String imageUrl
    ) {
        Activity activity = new Activity();
        activity.setActor(actor);
        activity.setType(type);
        activity.setAction(resolveAction(type));
        activity.setTargetId(targetId);
        activity.setPayload(toJsonPayload(payload, productId, productName, shadeId, shadeName, imageUrl));
        activityRepository.save(activity);
        return ResponseEntity.ok().build();
    }

    private String toJsonPayload(
            String message,
            Long productId,
            String productName,
            Long shadeId,
            String shadeName,
            String imageUrl
    ) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("message", message);
        if (productId != null) {
            payload.put("productId", productId);
        }
        if (productName != null && !productName.isBlank()) {
            payload.put("productName", productName);
        }
        if (shadeId != null) {
            payload.put("shadeId", shadeId);
        }
        if (shadeName != null && !shadeName.isBlank()) {
            payload.put("shadeName", shadeName);
        }
        if (imageUrl != null && !imageUrl.isBlank()) {
            payload.put("media", Map.of(
                    "imageUrl", imageUrl,
                    "imageSource", shadeId != null ? "SHADE" : "PRODUCT"
            ));
        }

        try {
            return OBJECT_MAPPER.writeValueAsString(payload);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize activity payload", exception);
        }
    }

    private ActivityAction resolveAction(ActivityType type) {
        return switch (type) {
            case REVIEW_CREATED, ROUTINE_CREATED ->
                ActivityAction.CREATED;
            case REVIEW_EDITED ->
                ActivityAction.EDITED;
            case ROUTINE_ITEM_ADDED, ROUTINE_IMAGE_ADDED, WISHLIST_ITEM_ADDED, BREAKOUTLIST_ITEM_ADDED, BREAKOUTLIST_INGREDIENT_ADDED ->
                ActivityAction.ADDED;
            case ROUTINE_ITEM_REMOVED, WISHLIST_ITEM_REMOVED, BREAKOUTLIST_ITEM_REMOVED, BREAKOUTLIST_INGREDIENT_REMOVED ->
                ActivityAction.REMOVED;
        };
    }

    public Page<ActivityDTO> getActivitiesByUsername(String username) {
        Page<Activity> activities = activityRepository.findByActor_Username(username,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        return activities.map(DTOMapper::toActivityDTO);
    }

    public Page<ActivityDTO> getFollowingActivitiesByUsername(String username) {
        Page<UserFollow> following = followRepository.findByFollowerUsername(username, PageRequest.of(0, 1000));
        List<String> followedUsernames = following.getContent().stream()
                .map(f -> f.getFollowed().getUsername()).collect(Collectors.toList());

        if (followedUsernames.isEmpty()) {
            return Page.<ActivityDTO>empty(PageRequest.of(0, 10));
        }

        Page<Activity> activities = activityRepository.findByActor_UsernameIn(followedUsernames,
                PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "createdAt")));
        return activities.map(DTOMapper::toActivityDTO);
    }
}
