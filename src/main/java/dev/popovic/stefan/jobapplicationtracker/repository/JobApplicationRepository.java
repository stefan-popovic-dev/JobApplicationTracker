package dev.popovic.stefan.jobapplicationtracker.repository;

import dev.popovic.stefan.jobapplicationtracker.entity.AppUser;
import dev.popovic.stefan.jobapplicationtracker.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobApplicationRepository extends JpaRepository<JobApplication, UUID> {

    List<JobApplication> findAllByAppUser(AppUser appUser);

    Optional<JobApplication> findByIdAndAppUser(UUID id, AppUser appUser);
}
