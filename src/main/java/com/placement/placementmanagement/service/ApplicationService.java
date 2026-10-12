package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.ApplicationStatus;
import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.ApplicationRepository;
import com.placement.placementmanagement.repository.JobRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final EligibilityService eligibilityService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentRepository studentRepository,
                              JobRepository jobRepository,
                              EligibilityService eligibilityService) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.eligibilityService = eligibilityService;
    }

    public Application applyForJob(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"));

        if (applicationRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already applied for this job"
            );
        }

        if (!eligibilityService.isEligible(student, job)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Student is not eligible for this job"
            );
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus(ApplicationStatus.APPLIED);

        return applicationRepository.save(application);
    }

    public Application getApplicationById(Long applicationId) {
        return applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));
    }

    public List<Application> getApplicationsByStudent(Long studentId) {

        return applicationRepository.findByStudentId(studentId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {

        return applicationRepository.findByJobId(jobId);
    }

    public Application updateStatus(Long applicationId, ApplicationStatus status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        if (status == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Status is required"
            );
        }

        ApplicationStatus currentStatus = application.getStatus();

        if (currentStatus == ApplicationStatus.REJECTED
                || currentStatus == ApplicationStatus.SELECTED) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot update an application that is already "
                            + currentStatus
            );
        }

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    public void deleteApplication(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        applicationRepository.delete(application);
    }
}