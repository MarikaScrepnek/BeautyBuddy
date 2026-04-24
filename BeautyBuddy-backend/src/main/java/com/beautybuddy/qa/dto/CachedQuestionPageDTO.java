package com.beautybuddy.qa.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public record CachedQuestionPageDTO(
    List<DisplayQuestionWithAnswersDTO> content,
    int page,
    int size,
    long totalElements
) {
    public static CachedQuestionPageDTO fromPage(Page<DisplayQuestionWithAnswersDTO> source) {
        return new CachedQuestionPageDTO(
            new ArrayList<>(source.getContent()),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements()
        );
    }

    public Page<DisplayQuestionWithAnswersDTO> toPage() {
        return new PageImpl<>(
            new ArrayList<>(content),
            PageRequest.of(page, size),
            totalElements
        );
    }
}