package com.beautybuddy.routine;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.beautybuddy.routine.dto.AddToRoutineRequestDTO;
import com.beautybuddy.routine.dto.CreateMakeupRoutineRequestDTO;
import com.beautybuddy.routine.dto.DisplayRoutineDTO;
import com.beautybuddy.security.CustomUserDetails;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {
    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping("/makeup")
    public ResponseEntity<List<DisplayRoutineDTO>> getMakeupRoutines(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<DisplayRoutineDTO> routines = routineService.getMakeupRoutines(userDetails.getEmail());
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/skincare")
    public ResponseEntity<List<DisplayRoutineDTO>> getSkincareRoutines(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<DisplayRoutineDTO> routines = routineService.getSkincareRoutines(userDetails.getEmail());
        return ResponseEntity.ok(routines);
    }

    @GetMapping("/haircare")
    public ResponseEntity<DisplayRoutineDTO> getHaircareRoutines(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        DisplayRoutineDTO routine = routineService.getHaircareRoutine(userDetails.getEmail());
        return ResponseEntity.ok(routine);
    }

    @GetMapping("/search")
    public String searchRoutines() {
        return "This will return routines matching the search query.";
    }

    @PostMapping("/makeup")
    public ResponseEntity<Void> createMakeupRoutine(Authentication authentication, @RequestBody CreateMakeupRoutineRequestDTO request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        routineService.createMakeupRoutine(userDetails.getEmail(), request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisplayRoutineDTO> updateRoutine(Authentication authentication, @PathVariable Long id, @RequestBody DisplayRoutineDTO request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        DisplayRoutineDTO updatedRoutine = routineService.updateRoutine(userDetails.getEmail(), request);
        return ResponseEntity.ok(updatedRoutine);
    }

    @DeleteMapping("/{id}")
    public String deleteMakeupRoutine() {
        return "This will soft delete the makeup routine with the given ID.";
    }

    @PostMapping("/{id}/add-product")
    public ResponseEntity<Void> addProductToRoutine(Authentication authentication, @PathVariable Long id, @RequestBody AddToRoutineRequestDTO request) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        routineService.addProductToRoutine(userDetails.getEmail(), id, request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/update-product")
    public String updateProductInRoutine() {
        return "This will update a product in the routine with the given ID.";
    }

    @DeleteMapping("/{routineId}/{productId}")
    public ResponseEntity<Void> removeProductFromRoutine(Authentication authentication, @PathVariable Long routineId, @PathVariable Long productId) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        routineService.removeProductFromRoutine(userDetails.getEmail(), routineId, productId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items")
    public ResponseEntity<List<Long>> getAllRoutineItems(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        List<Long> items = routineService.getAllRoutineItems(userDetails.getEmail());
        return ResponseEntity.ok(items);
    }
}
