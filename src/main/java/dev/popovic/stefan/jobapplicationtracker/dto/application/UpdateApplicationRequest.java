package dev.popovic.stefan.jobapplicationtracker.dto.application;

import dev.popovic.stefan.jobapplicationtracker.entity.ApplicationStatus;

import java.time.LocalDate;

public record UpdateApplicationRequest(
        String company,
        String role,
        ApplicationStatus status,
        LocalDate appliedDate,
        String notes,
        String jobDescription
) {}
