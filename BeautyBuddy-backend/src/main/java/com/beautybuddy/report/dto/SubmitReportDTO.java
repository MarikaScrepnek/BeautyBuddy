package com.beautybuddy.report.dto;

public record SubmitReportDTO(
    String targetType,
    Long targetId,
    String reason
) {
    
}
