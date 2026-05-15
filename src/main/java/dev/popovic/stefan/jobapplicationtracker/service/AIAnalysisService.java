package dev.popovic.stefan.jobapplicationtracker.service;

import dev.popovic.stefan.jobapplicationtracker.dto.ai.AnalysisRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.ai.AnalysisResponse;
import dev.popovic.stefan.jobapplicationtracker.entity.AppUser;
import dev.popovic.stefan.jobapplicationtracker.entity.JobApplication;
import dev.popovic.stefan.jobapplicationtracker.repository.AppUserRepository;
import dev.popovic.stefan.jobapplicationtracker.repository.JobApplicationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Slf4j
@Service
public class AIAnalysisService {

    private static final String SYSTEM_PROMPT = """
            You are a career coach performing a resume gap analysis. Given a job description
            and a candidate's resume text, identify: 1) Skills or experience in the JD not
            demonstrated in the resume. 2) Strengths the candidate should emphasize.
            3) An overall fit score from 1 to 10 with a one-sentence justification.
            Be specific and actionable. Format your response with clear sections.""";

    private final RestClient restClient;
    private final AppUserRepository appUserRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public AIAnalysisService(
            @Value("${openai.api-key}") String apiKey,
            AppUserRepository appUserRepository,
            JobApplicationRepository jobApplicationRepository) {
        this.restClient = RestClient.builder()
                .baseUrl("https://api.openai.com/v1")
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
        this.appUserRepository = appUserRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    public AnalysisResponse analyze(AnalysisRequest request, String email) {
        String jobDescription = resolveJobDescription(request, email);
        String userMessage = "Job Description:\n" + jobDescription + "\n\nResume:\n" + request.resumeText();

        OpenAIRequest openAIRequest = new OpenAIRequest(
                "gpt-4o-mini",
                List.of(
                        new OpenAIMessage("system", SYSTEM_PROMPT),
                        new OpenAIMessage("user", userMessage)
                )
        );

        try {
            OpenAIResponse response = restClient.post()
                    .uri("/chat/completions")
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(openAIRequest)
                    .retrieve()
                    .body(OpenAIResponse.class);

            String analysis = response.choices().get(0).message().content();
            log.debug("OpenAI gap analysis completed for user={}", email);
            return new AnalysisResponse(analysis);
        } catch (Exception e) {
            log.error("OpenAI call failed: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "AI service unavailable");
        }
    }

    private String resolveJobDescription(AnalysisRequest request, String email) {
        if (request.applicationId() == null) {
            return request.jobDescription();
        }
        AppUser appUser = appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        JobApplication application = jobApplicationRepository
                .findByIdAndAppUser(request.applicationId(), appUser)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
        String saved = application.getJobDescription();
        if (saved == null || saved.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Application has no job description stored");
        }
        return saved;
    }

    private record OpenAIMessage(String role, String content) {}

    private record OpenAIRequest(String model, List<OpenAIMessage> messages) {}

    private record OpenAIChoice(OpenAIMessage message) {}

    private record OpenAIResponse(List<OpenAIChoice> choices) {}
}
