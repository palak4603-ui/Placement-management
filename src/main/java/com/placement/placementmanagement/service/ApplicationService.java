package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.ApplicationRepository;
import com.placement.placementmanagement.repository.JobRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.placement.placementmanagement.service.EligibiltyService;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final EligibiltyService eligibiltyService;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentRepository studentRepository,
                              JobRepository jobRepository,
                              EligibiltyService eligibiltyService) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.eligibiltyService = eligibiltyService;
    }


    public Application applyForJob(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (applicationRepository.existsByStudentIdAndJobId(studentId, jobId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already applied for this job"
            );
        }

        if (!eligibiltyService.isEligible(student, job)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Student is not eligible for this job"
            );
        }

        Application application = new Application();
        application.setStudent(student);
        application.setJob(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus("APPLIED");

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


    public Application updateStatus(Long applicationId, String status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Application not found"
                ));

        if (status == null
                || (!status.equals("APPLIED")
                && !status.equals("SHORTLISTED")
                && !status.equals("REJECTED")
                && !status.equals("SELECTED"))) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid application status"
            );
        }

        String currentStatus = application.getStatus();

        if (currentStatus.equals("REJECTED")
                || currentStatus.equals("SELECTED")) {

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