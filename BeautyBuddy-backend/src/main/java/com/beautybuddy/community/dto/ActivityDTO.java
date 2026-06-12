package com.beautybuddy.community.dto;

import com.beautybuddy.community.activity.entity.ActivityAction;
import com.beautybuddy.community.activity.entity.ActivityType;

public record ActivityDTO(
        Long id,
        ActivityType type,
        ActivityAction action,
        String actorUsername,
        String payload,
        String timestamp
        ) {

}
