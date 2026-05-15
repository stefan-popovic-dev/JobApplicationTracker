package dev.popovic.stefan.jobapplicationtracker.dto.contact;

import jakarta.validation.constraints.NotBlank;

public record CreateContactRequest(
        @NotBlank String name,
        String email,
        String phone,
        String notes
) {}
