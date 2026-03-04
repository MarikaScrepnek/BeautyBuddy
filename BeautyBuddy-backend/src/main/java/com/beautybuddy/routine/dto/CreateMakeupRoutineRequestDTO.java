package com.beautybuddy.routine.dto;

import com.beautybuddy.routine.OccasionEnum;

public record CreateMakeupRoutineRequestDTO(
    OccasionEnum occasion,
    String name,
    String notes
) {}
