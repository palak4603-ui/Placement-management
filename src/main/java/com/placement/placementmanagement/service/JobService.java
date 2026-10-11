package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.repository.JobRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    public Job saveJob(Job job) {
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
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "CGPA must be between 0 and 10"
            );
        }

        if (branch == null || branch.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
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
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "CGPA must be between 0 and 10"
            );
        }

        if (branch == null || branch.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Branch must not be empty"
            );
        }

        if (keyword == null || keyword.trim().isEmpty()) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
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
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}