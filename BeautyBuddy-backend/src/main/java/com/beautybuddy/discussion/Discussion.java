package com.beautybuddy.discussion;

import com.beautybuddy.common.entity.UserWrittenEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(
    name = "discussion"
)
public class Discussion extends UserWrittenEntity{
    
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "reply_count", nullable = false)
    private int replyCount = 0;

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public int getReplyCount() {
        return replyCount;
    }
    public void setReplyCount(int replyCount) {
        this.replyCount = replyCount;
    }
}
