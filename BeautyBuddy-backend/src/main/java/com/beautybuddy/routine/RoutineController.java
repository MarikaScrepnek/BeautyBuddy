package com.beautybuddy.routine;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/routines")
public class RoutineController {
    private final RoutineService routineService;

    public RoutineController(RoutineService routineService) {
        this.routineService = routineService;
    }

    @GetMapping
    public String getRoutines() {
        return "This will return the user's routines.";
    }

    @GetMapping("/search")
    public String searchRoutines() {
        return "This will return routines matching the search query.";
    }

    @PostMapping
    public String createRoutine() {
        return "This will create a new routine for the user.";
    }

    @PutMapping("/{id}")
    public String updateRoutine() {
        return "This will update the routine with the given ID.";
    }

    @DeleteMapping("/{id}")
    public String deleteRoutine() {
        return "This will soft delete the routine with the given ID.";
    }

    @PostMapping("/{id}/add-product")
    public String addProductToRoutine() {
        return "This will add a product to the routine with the given ID.";
    }

    @PutMapping("/{id}/update-product")
    public String updateProductInRoutine() {
        return "This will update a product in the routine with the given ID.";
    }

    @DeleteMapping("/{id}/remove-product")
    public String removeProductFromRoutine() {
        return "This will remove a product from the routine with the given ID.";
    }
}
