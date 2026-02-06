package com.beautybuddy.user;

import java.time.LocalDateTime;

import com.beautybuddy.wishlist.Wishlist;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToOne;


@Entity
@Table(name = "account")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int userId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false, columnDefinition = "CITEXT")
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime dateJoined = LocalDateTime.now();

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Wishlist wishlist;

    public User() {}

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public LocalDateTime getDateJoined() { return dateJoined; }
    public void setDateJoined(LocalDateTime dateJoined) { this.dateJoined = dateJoined; }

    public Wishlist getWishlist() { return wishlist; }

    public void setWishlist(Wishlist wishlist) { this.wishlist = wishlist; }
}