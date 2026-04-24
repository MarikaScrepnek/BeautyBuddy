package com.beautybuddy.review.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public record CachedReviewPageDTO(
    List<DisplayReviewDTO> content,
    int page,
    int size,
    long totalElements
) {
    public static CachedReviewPageDTO fromPage(Page<DisplayReviewDTO> source) {
        return new CachedReviewPageDTO(
            new ArrayList<>(source.getContent()),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements()
        );
    }

    public Page<DisplayReviewDTO> toPage() {
        return new PageImpl<>(
            new ArrayList<>(content),
            PageRequest.of(page, size),
            totalElements
        );
    }
}