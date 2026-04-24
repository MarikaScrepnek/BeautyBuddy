package com.beautybuddy.discussion.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class CachedDiscussionPageDTO {
    private List<DisplayDiscussionDTO> content;
    private int page;
    private int size;
    private long totalElements;

    public CachedDiscussionPageDTO() {
        this.content = new ArrayList<>();
    }

    public CachedDiscussionPageDTO(List<DisplayDiscussionDTO> content, int page, int size, long totalElements) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
    }

    public static CachedDiscussionPageDTO fromPage(Page<DisplayDiscussionDTO> source) {
        return new CachedDiscussionPageDTO(
            new ArrayList<>(source.getContent()),
            source.getNumber(),
            source.getSize(),
            source.getTotalElements()
        );
    }

    public Page<DisplayDiscussionDTO> toPage() {
        List<DisplayDiscussionDTO> safeContent = content == null ? new ArrayList<>() : new ArrayList<>(content);
        int safePage = Math.max(0, page);
        int safeSize = size > 0 ? size : 1;
        return new PageImpl<>(
            safeContent,
            PageRequest.of(safePage, safeSize),
            totalElements
        );
    }

    public List<DisplayDiscussionDTO> getContent() {
        return content;
    }

    public void setContent(List<DisplayDiscussionDTO> content) {
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