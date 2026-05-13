package com.beautybuddy.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beautybuddy.user.dto.UserSearchDTO;
import com.beautybuddy.user.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepo;

    public UserService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public List<UserSearchDTO> searchUsers(String query) {
        String lowerQuery = query.toLowerCase();
        return userRepo.findAll().stream()
                .filter(user -> user.getUsername().toLowerCase().contains(lowerQuery)
                || (user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerQuery)))
                .map(user -> new UserSearchDTO(
                user.getUsername(),
                user.getAvatarLink()))
                .collect(Collectors.toList());
    }
}
