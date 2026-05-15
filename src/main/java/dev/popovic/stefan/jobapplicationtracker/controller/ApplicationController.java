package dev.popovic.stefan.jobapplicationtracker.controller;

import dev.popovic.stefan.jobapplicationtracker.dto.application.ApplicationResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.application.CreateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.application.UpdateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.service.ApplicationService;
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
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    public ResponseEntity<List<ApplicationResponse>> getAll(
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.getAllForUser(principal.getUsername()));
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> create(
            @Valid @RequestBody CreateApplicationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        ApplicationResponse response = applicationService.create(request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApplicationResponse> getById(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.getByIdForUser(id, principal.getUsername()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApplicationResponse> update(
            @PathVariable UUID id,
            @RequestBody UpdateApplicationRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(applicationService.update(id, request, principal.getUsername()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID id,
            @AuthenticationPrincipal UserDetails principal) {
        applicationService.delete(id, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
