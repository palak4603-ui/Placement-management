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

            return jobRepository.save(job);
        }

        return null;
    }
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }
}