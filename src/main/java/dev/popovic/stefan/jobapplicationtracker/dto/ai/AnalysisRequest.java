package dev.popovic.stefan.jobapplicationtracker.dto.ai;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record AnalysisRequest(
        @NotBlank String jobDescription,
        @NotBlank String resumeText,
        UUID applicationId
) {}
