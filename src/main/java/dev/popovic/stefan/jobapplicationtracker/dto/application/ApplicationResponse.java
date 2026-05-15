package dev.popovic.stefan.jobapplicationtracker.dto.application;

import dev.popovic.stefan.jobapplicationtracker.entity.ApplicationStatus;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

public record ApplicationResponse(
        UUID id,
        String company,
        String role,
        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        String jobDescription,
        Instant createdAt,
        Instant updatedAt
) {}
