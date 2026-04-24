package com.beautybuddy.routine.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

public record DisplayRoutineDTO(
    Long routineId,
    String name, //can be null
    String author,
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    LocalDateTime updatedAt,
    String timeOfDay, //can be null
    String occasion, //can be null
    String notes,
    List<DisplayRoutineItemDTO> items
) {}
