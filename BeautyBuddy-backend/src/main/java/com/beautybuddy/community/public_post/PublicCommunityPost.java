package com.beautybuddy.community.public_post;

import com.beautybuddy.common.entity.SoftDeletableEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "public_community_post")
public class PublicCommunityPost extends SoftDeletableEntity{
    
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "media")
    private String media;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getMedia() { return media; }
    public void setMedia(String media) { this.media = media; }
}
