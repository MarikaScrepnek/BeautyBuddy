package com.beautybuddy.user.dto;

import com.beautybuddy.user.enums.CountryEnum;
import com.beautybuddy.user.enums.HairDensityEnum;
import com.beautybuddy.user.enums.HairTextureEnum;
import com.beautybuddy.user.enums.PronounEnum;
import com.beautybuddy.user.enums.SkinConditionEnum;
import com.beautybuddy.user.enums.SkinTypeEnum;

public record UserSearchDTO(
        String username,
        String profilePictureUrl,
        boolean isFollowing,
        boolean isFollower,
        boolean isCurrentUser,
        PronounEnum pronoun,
        CountryEnum country,
        HairDensityEnum hairDensity,
        HairTextureEnum hairTexture,
        SkinTypeEnum skinType,
        SkinConditionEnum skinCondition
        ) {

}
