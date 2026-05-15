package dev.popovic.stefan.jobapplicationtracker.dto.application;

import dev.popovic.stefan.jobapplicationtracker.entity.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record CreateApplicationRequest(
        @NotBlank String company,
        @NotBlank String role,
        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        String jobDescription
) {}
