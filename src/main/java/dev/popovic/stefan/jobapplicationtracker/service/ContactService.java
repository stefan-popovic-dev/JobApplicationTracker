package dev.popovic.stefan.jobapplicationtracker.service;

import dev.popovic.stefan.jobapplicationtracker.dto.contact.ContactResponse;
import dev.popovic.stefan.jobapplicationtracker.dto.contact.CreateContactRequest;
import dev.popovic.stefan.jobapplicationtracker.entity.AppUser;
import dev.popovic.stefan.jobapplicationtracker.entity.Contact;
import dev.popovic.stefan.jobapplicationtracker.entity.JobApplication;
import dev.popovic.stefan.jobapplicationtracker.repository.AppUserRepository;
import dev.popovic.stefan.jobapplicationtracker.repository.ContactRepository;
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
public class ContactService {

    private final ContactRepository contactRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final AppUserRepository appUserRepository;

    public List<ContactResponse> getAllForApplication(UUID applicationId, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = resolveApplication(applicationId);
        verifyOwnership(application, appUser);
        return contactRepository.findAllByJobApplication(application)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public ContactResponse create(UUID applicationId, CreateContactRequest request, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = resolveApplication(applicationId);
        verifyOwnership(application, appUser);
        Contact contact = Contact.builder()
                .jobApplication(application)
                .name(request.name())
                .email(request.email())
                .phone(request.phone())
                .notes(request.notes())
                .build();
        Contact saved = contactRepository.save(contact);
        log.debug("Created Contact id={} for application={}", saved.getId(), applicationId);
        return toResponse(saved);
    }

    public void delete(UUID applicationId, UUID contactId, String email) {
        AppUser appUser = resolveUser(email);
        JobApplication application = resolveApplication(applicationId);
        verifyOwnership(application, appUser);
        Contact contact = contactRepository.findByIdAndJobApplication(contactId, application)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Contact not found"));
        contactRepository.delete(contact);
        log.debug("Deleted Contact id={} from application={}", contactId, applicationId);
    }

    private AppUser resolveUser(String email) {
        return appUserRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    private JobApplication resolveApplication(UUID applicationId) {
        return jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Application not found"));
    }

    private void verifyOwnership(JobApplication application, AppUser appUser) {
        if (!application.getAppUser().getId().equals(appUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
    }

    private ContactResponse toResponse(Contact contact) {
        return new ContactResponse(
                contact.getId(),
                contact.getName(),
                contact.getEmail(),
                contact.getPhone(),
                contact.getNotes()
        );
    }
}
