package dev.popovic.stefan.jobapplicationtracker.dto.contact;

import java.util.UUID;

public record ContactResponse(
        UUID id,
        String name,
        String email,
        String phone,
        String notes
) {}
