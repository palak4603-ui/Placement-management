package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.repository.ApplicationRepository;
import com.placement.placementmanagement.repository.JobRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;

    public JobService(JobRepository jobRepository,
                      ApplicationRepository applicationRepository) {
        this.jobRepository = jobRepository;
        this.applicationRepository = applicationRepository;
    }

    public Job saveJob(Job job) {

        // A POST must always create a new job, never overwrite one
        job.setId(null);

        return jobRepository.save(job);
    }

    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }

    public List<Job> searchJobs(String keyword) {
        return jobRepository
                .findByTitleContainingIgnoreCaseOrCompanyContainingIgnoreCase(
                        keyword, keyword
                );
    }

    public List<Job> filterJobsByEligibility(Double cgpa, String branch) {

        if (cgpa == null || cgpa < 0 || cgpa > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CGPA must be between 0 and 10"
            );
        }

        if (branch == null || branch.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Branch must not be empty"
            );
        }

        return jobRepository.filterEligibleJobs(
                cgpa,
                branch.trim()
        );
    }

    public List<Job> searchAndFilterJobs(
            String keyword,
            Double cgpa,
            String branch) {

        if (cgpa == null || cgpa < 0 || cgpa > 10) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "CGPA must be between 0 and 10"
            );
        }

        if (branch == null || branch.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Branch must not be empty"
            );
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Keyword must not be empty"
            );
        }

        return jobRepository.searchAndFilterJobs(
                keyword.trim(),
                cgpa,
                branch.trim()
        );
    }

    public Optional<Job> getJobById(Long id) {
        return jobRepository.findById(id);
    }

    public Job updateJob(Long id, Job updatedJob) {

        Optional<Job> existingJob = jobRepository.findById(id);

        if (existingJob.isPresent()) {

            Job job = existingJob.get();

            job.setTitle(updatedJob.getTitle());
            job.setCompany(updatedJob.getCompany());
            job.setLocation(updatedJob.getLocation());
            job.setDescription(updatedJob.getDescription());
            job.setRequiredSkills(updatedJob.getRequiredSkills());
            job.setMinimumCgpa(updatedJob.getMinimumCgpa());
            job.setEligibleBranches(updatedJob.getEligibleBranches());
            job.setMaximumBacklogs(updatedJob.getMaximumBacklogs());

            return jobRepository.save(job);
        }

        return null;
    }

    public boolean deleteJob(Long id) {
        if (!jobRepository.existsById(id)) {
            return false;
        }

        if (applicationRepository.existsByJobId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a job that has applications. "
                            + "Delete the applications first."
            );
        }

        jobRepository.deleteById(id);
        return true;
    }
}