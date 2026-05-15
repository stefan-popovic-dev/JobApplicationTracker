package dev.popovic.stefan.jobapplicationtracker.controller;

import dev.popovic.stefan.jobapplicationtracker.dto.ai.AnalysisRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.ai.AnalysisResponse;
import dev.popovic.stefan.jobapplicationtracker.service.AIAnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
@Tag(name = "AI Analysis")
@SecurityRequirement(name = "bearerAuth")
public class AIAnalysisController {

    private final AIAnalysisService aiAnalysisService;

    @Operation(summary = "Analyze skill gaps between a resume and a job description")
    @PostMapping("/analyze")
    public ResponseEntity<AnalysisResponse> analyze(
            @Valid @RequestBody AnalysisRequest request,
            @AuthenticationPrincipal UserDetails principal) {
        return ResponseEntity.ok(aiAnalysisService.analyze(request, principal.getUsername()));
    }
}
