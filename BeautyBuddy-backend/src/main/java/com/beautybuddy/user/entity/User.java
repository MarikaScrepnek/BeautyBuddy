package com.beautybuddy.user.entity;

import com.beautybuddy.common.entity.SoftDeletableEntity;
import com.beautybuddy.wishlist.Wishlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;


@Entity
@Table(name = "account")
public class User extends SoftDeletableEntity{

    @Column(name = "username", unique = true, nullable = false, columnDefinition = "CITEXT")
    private String username;

    @Column(name = "email", unique = true, nullable = false, columnDefinition = "CITEXT")
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "avatar_link")
    private String avatarLink;

    @Column(name = "is_private", nullable = false)
    private boolean isPrivate;

    @Column(name = "followers_count", nullable = false)
    private int followersCount;

    @Column(name = "following_count", nullable = false)
    private int followingCount;

    @Column(name = "unread_notifications_count", nullable = false)
    private int unreadNotificationsCount = 0;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wishlist wishlist;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getAvatarLink() { return avatarLink; }
    public void setAvatarLink(String avatarLink) { this.avatarLink = avatarLink; }

    public boolean isPrivate() { return isPrivate; }
    public void setPrivate(boolean isPrivate) { this.isPrivate = isPrivate; }

    public int getFollowersCount() { return followersCount; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }

    public int getFollowingCount() { return followingCount; }
    public void setFollowingCount(int followingCount) { this.followingCount = followingCount; }

    public int getUnreadNotificationsCount() { return unreadNotificationsCount; }
    public void setUnreadNotificationsCount(int unreadNotificationsCount) { this.unreadNotificationsCount = unreadNotificationsCount; }

    public Wishlist getWishlist() { return wishlist; }
    public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }
}