package dev.popovic.stefan.jobapplicationtracker.repository;

import dev.popovic.stefan.jobapplicationtracker.entity.Contact;
import dev.popovic.stefan.jobapplicationtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ContactRepository extends JpaRepository<Contact, UUID> {

    List<Contact> findAllByJobApplication(JobApplication jobApplication);

    Optional<Contact> findByIdAndJobApplication(UUID id, JobApplication jobApplication);
}
