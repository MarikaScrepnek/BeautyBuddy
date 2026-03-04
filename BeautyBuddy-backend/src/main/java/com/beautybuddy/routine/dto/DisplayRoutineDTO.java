package com.beautybuddy.routine.dto;

import java.time.LocalDateTime;
import java.util.List;

public record DisplayRoutineDTO(
    Long routineId,
    String name, //can be null
    String author,
    LocalDateTime updatedAt,
    String occasion, //can be null
    String notes,
    List<DisplayRoutineItemDTO> items
) {}
