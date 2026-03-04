package com.beautybuddy.routine.dto;

public record AddToRoutineRequestDTO(
    Long productId,
    String shadeName,
    String targetRoutineType, // "makeup", "skincare", or "haircare"
    String routineId
) {}
