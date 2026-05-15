package dev.popovic.stefan.jobapplicationtracker.controller;

import dev.popovic.stefan.jobapplicationtracker.dto.contact.ContactResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.contact.CreateContactRequest;
import dev.popovic.stefan.jobapplicationtracker.service.ContactService;
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
@RequestMapping("/applications/{appId}/contacts")
@RequiredArgsConstructor
@Tag(name = "Contacts")
@SecurityRequirement(name = "bearerAuth")
public class ContactController {

    private final ContactService contactService;

    @Operation(summary = "List all contacts for a job application")
    @GetMapping
    public ResponseEntity<List<ContactResponse>> getAll(
            @PathVariable UUID appId,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(contactService.getAllForApplication(appId, principal.getUsername()));
    }

    @Operation(summary = "Add a contact to a job application")
    @PostMapping
    public ResponseEntity<ContactResponse> create(
            @PathVariable UUID appId,
            @Valid @RequestBody CreateContactRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        ContactResponse response = contactService.create(appId, request, principal.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Delete a contact from a job application")
    @DeleteMapping("/{contactId}")
    public ResponseEntity<Void> delete(
            @PathVariable UUID appId,
            @PathVariable UUID contactId,
            @AuthenticationPrincipal UserDetails principal) {
        contactService.delete(appId, contactId, principal.getUsername());
        return ResponseEntity.noContent().build();
    }
}
