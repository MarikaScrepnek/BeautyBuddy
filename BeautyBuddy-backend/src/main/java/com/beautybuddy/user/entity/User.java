package com.beautybuddy.user.entity;

import java.time.LocalDate;

import com.beautybuddy.breakout.entity.BreakoutList;
import com.beautybuddy.common.entity.SoftDeletableEntity;
import com.beautybuddy.wishlist.entity.Wishlist;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "account")
public class User extends SoftDeletableEntity {

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

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "pronouns")
    private String pronouns;

    @Column(name = "bio")
    private String bio;

    @Column(name = "hair_texture")
    private String hairTexture;

    @Column(name = "hair_density")
    private String hairDensity;

    @Column(name = "skin_type")
    private String skinType;

    @Column(name = "skin_condition")
    private String skinCondition;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wishlist wishlist;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private BreakoutList breakoutList;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getAvatarLink() {
        return avatarLink;
    }

    public void setAvatarLink(String avatarLink) {
        this.avatarLink = avatarLink;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public void setFollowersCount(int followersCount) {
        this.followersCount = followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    public void setFollowingCount(int followingCount) {
        this.followingCount = followingCount;
    }

    public int getUnreadNotificationsCount() {
        return unreadNotificationsCount;
    }

    public void setUnreadNotificationsCount(int unreadNotificationsCount) {
        this.unreadNotificationsCount = unreadNotificationsCount;
    }

    public Wishlist getWishlist() {
        return wishlist;
    }

    public void setWishlist(Wishlist wishlist) {
        this.wishlist = wishlist;
    }

    public BreakoutList getBreakoutList() {
        return breakoutList;
    }

    public void setBreakoutList(BreakoutList breakoutList) {
        this.breakoutList = breakoutList;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPronouns() {
        return pronouns;
    }

    public void setPronouns(String pronouns) {
        this.pronouns = pronouns;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getHairTexture() {
        return hairTexture;
    }

    public void setHairTexture(String hairTexture) {
        this.hairTexture = hairTexture;
    }

    public String getHairDensity() {
        return hairDensity;
    }

    public void setHairDensity(String hairDensity) {
        this.hairDensity = hairDensity;
    }

    public String getSkinType() {
        return skinType;
    }

    public void setSkinType(String skinType) {
        this.skinType = skinType;
    }

    public String getSkinCondition() {
        return skinCondition;
    }

    public void setSkinCondition(String skinCondition) {
        this.skinCondition = skinCondition;
    }
}
