package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.ApplicationRepository;
import com.placement.placementmanagement.repository.JobRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentRepository studentRepository,
                              JobRepository jobRepository) {
        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    public Application applyForJob(Long studentId, Long jobId) {

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        Application application = new Application();

        application.setStudent(student);
        application.setJob(job);
        application.setAppliedDate(LocalDate.now());
        application.setStatus("APPLIED");

        return applicationRepository.save(application);
    }

    public List<Application> getApplicationsByStudent(Long studentId) {

        return applicationRepository.findByStudentId(studentId);
    }

    public List<Application> getApplicationsByJob(Long jobId) {

        return applicationRepository.findByJobId(jobId);
    }

    public Application updateStatus(Long applicationId, String status) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);

        return applicationRepository.save(application);
    }

    public void deleteApplication(Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        applicationRepository.delete(application);
    }
}