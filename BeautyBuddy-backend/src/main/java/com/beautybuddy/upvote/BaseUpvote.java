package com.beautybuddy.upvote;

import com.beautybuddy.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@MappedSuperclass
public abstract class BaseUpvote {

	@ManyToOne
	@JoinColumn(name = "account_id", nullable = false)
	private User user;

	@Column(name = "created_at", nullable = false, insertable = false, updatable = false)
	private LocalDateTime createdAt;

	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
}
