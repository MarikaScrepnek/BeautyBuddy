package com.beautybuddy.breakout;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.breakout.dto.AddToBreakoutListDTO;
import com.beautybuddy.security.CustomUserDetails;

@RestController
@RequestMapping("/api/breakout-list")
public class BreakoutListController {
    private final BreakoutListService breakoutListService;

    public BreakoutListController(BreakoutListService breakoutListService) {
        this.breakoutListService = breakoutListService;
    }

    @PostMapping("/add")
    public void addToBreakoutList(Authentication authentication, @RequestBody AddToBreakoutListDTO addToBreakoutListDTO) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("User must be authenticated to add to breakout list");
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        breakoutListService.addToBreakoutList(userDetails.getEmail(), addToBreakoutListDTO);
    }

    @GetMapping
    public void getBreakoutList() {
    }

    @DeleteMapping("/remove")
    public void removeFromBreakoutList() {
    }
}
