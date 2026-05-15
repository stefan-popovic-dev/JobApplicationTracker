package dev.popovic.stefan.jobapplicationtracker.service;

import dev.popovic.stefan.jobapplicationtracker.dto.application.ApplicationResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.application.CreateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.dto.application.UpdateApplicationRequest;
import dev.popovic.stefan.jobapplicationtracker.entity.AppUser;
import dev.popovic.stefan.jobapplicationtracker.entity.JobApplication;
import dev.popovic.stefan.jobapplicationtracker.repository.AppUserRepository;
import dev.popovic.stefan.jobapplicationtracker.repository.JobApplicationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final AppUserRepository appUserRepository;

    public List<ApplicationResponse> getAllForUser(String email) {
        AppUser appUser = resolveUser(email);
        return jobApplicationRepository.findAllByAppUser(appUser)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ApplicationResponse getByIdForUser(UUID id, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = findById(id);
        verifyOwnership(application, appUser);
        return toResponse(application);
    }

    public ApplicationResponse create(CreateApplicationRequest request, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = JobApplication.builder()
                .appUser(appUser)
                .company(request.company())
                .role(request.role())
                .status(request.status())
                .appliedDate(request.appliedDate())
                .notes(request.notes())
                .jobDescription(request.jobDescription())
                .build();
        JobApplication saved = jobApplicationRepository.save(application);
        log.debug("Created JobApplication id={} for user={}", saved.getId(), email);
        return toResponse(saved);
    }

    public ApplicationResponse update(UUID id, UpdateApplicationRequest request, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = findById(id);
        verifyOwnership(application, appUser);

        if (request.company() != null) application.setCompany(request.company());
        if (request.role() != null) application.setRole(request.role());
        if (request.status() != null) application.setStatus(request.status());
        if (request.appliedDate() != null) application.setAppliedDate(request.appliedDate());
        if (request.notes() != null) application.setNotes(request.notes());
        if (request.jobDescription() != null) application.setJobDescription(request.jobDescription());

        JobApplication saved = jobApplicationRepository.save(application);
        log.debug("Updated JobApplication id={} for user={}", saved.getId(), email);
        return toResponse(saved);
    }

    public void delete(UUID id, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = findById(id);
        verifyOwnership(application, appUser);
        jobApplicationRepository.delete(application);
        log.debug("Deleted JobApplication id={} for user={}", id, email);
    }

    private AppUser resolveUser(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private JobApplication findById(UUID id) {
        return jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private void verifyOwnership(JobApplication application, AppUser appUser) {
        if (!application.getAppUser().getId().equals(appUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private ApplicationResponse toResponse(JobApplication application) {
        return new ApplicationResponse(
                application.getId(),
                application.getCompany(),
                application.getRole(),
                application.getStatus(),
                application.getAppliedDate(),
                application.getNotes(),
                application.getJobDescription(),
                application.getCreatedAt(),
                application.getUpdatedAt()
        );
    }
}
