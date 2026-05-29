package com.beautybuddy.user;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.beautybuddy.common.DTOMapper;
import com.beautybuddy.user.dto.UserSearchDTO;
import com.beautybuddy.user.repo.FollowRepository;
import com.beautybuddy.user.repo.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepo;
    private final FollowRepository followRepo;

    public UserService(UserRepository userRepo, FollowRepository followRepo) {
        this.userRepo = userRepo;
        this.followRepo = followRepo;
    }

    public List<UserSearchDTO> searchUsers(String query, String currentUsername) {
        String lowerQuery = query.toLowerCase();
        return userRepo.findAll().stream()
                .filter(user -> user.getUsername() != null && user.getUsername().toLowerCase().contains(lowerQuery))
                .map(user -> DTOMapper.toUserSearchDTO(user, currentUsername, followRepo))
                .collect(Collectors.toList());
    }
}
