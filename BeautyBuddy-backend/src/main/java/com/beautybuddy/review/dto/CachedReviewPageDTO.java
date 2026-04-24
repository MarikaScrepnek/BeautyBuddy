package com.beautybuddy.review.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class CachedReviewPageDTO {
    private List<DisplayReviewDTO> content;
    private int page;
    private int size;
    private long totalElements;

    public CachedReviewPageDTO() {
        this.content = new ArrayList<>();
    }

    public CachedReviewPageDTO(List<DisplayReviewDTO> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public static CachedReviewPageDTO fromPage(Page<DisplayReviewDTO> source) {
        return new CachedReviewPageDTO(
            new ArrayList<>(source.getContent()),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements()
        );
    }

    public Page<DisplayReviewDTO> toPage() {
        List<DisplayReviewDTO> safeContent = content == null ? new ArrayList<>() : new ArrayList<>(content);
        int safePage = Math.max(0, page);
        int safeSize = size > 0 ? size : 1;
        return new PageImpl<>(
            safeContent,
            PageRequest.of(safePage, safeSize),
            totalElements
        );
    }

    public List<DisplayReviewDTO> getContent() {
        return content;
    }

    public void setContent(List<DisplayReviewDTO> content) {
        this.content = content;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}