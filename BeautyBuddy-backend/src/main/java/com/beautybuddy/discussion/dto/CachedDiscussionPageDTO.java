package com.beautybuddy.discussion.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public record CachedDiscussionPageDTO(
    List<DisplayDiscussionDTO> content,
    int page,
    int size,
    long totalElements
) {
    public static CachedDiscussionPageDTO fromPage(Page<DisplayDiscussionDTO> source) {
        return new CachedDiscussionPageDTO(
            new ArrayList<>(source.getContent()),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements()
        );
    }

    public Page<DisplayDiscussionDTO> toPage() {
        return new PageImpl<>(
            new ArrayList<>(content),
            PageRequest.of(page, size),
            totalElements
        );
    }
}