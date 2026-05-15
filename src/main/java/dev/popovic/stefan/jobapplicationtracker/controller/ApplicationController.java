package dev.popovic.stefan.jobapplicationtracker.controller;

import dev.popovic.stefan.jobapplicationtracker.dto.application.ApplicationResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.application.CreateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.application.UpdateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.service.ApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/applications")
@RequiredArgsConstructor
@Tag(name = "Job Applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @Operation(summary = "List all job applications for the authenticated user")
    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.getAllForUser(principal.getUsername()));
    }

    @Operation(summary = "Create a new job application")
    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @Valid @RequestBody CreateApplicationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        ApplicationResponse response = applicationService.create(request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get a job application by ID")
    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.getByIdForUser(id, principal.getUsername()));
    }

    @Operation(summary = "Update an existing job application")
    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateApplicationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.update(id, request, principal.getUsername()));
    }

    @Operation(summary = "Delete a job application")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        applicationService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
