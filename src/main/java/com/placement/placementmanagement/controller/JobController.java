package com.placement.placementmanagement.controller;

import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/api/jobs")
    public ResponseEntity<Job> createJob(@RequestBody @Valid Job job) {

        Job saved = jobService.saveJob(job);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    @GetMapping("/api/jobs")
    public ResponseEntity<List<Job>> getAllJobs() {
        List<Job> jobs = jobService.getAllJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/api/jobs/search")
    public ResponseEntity<List<Job>> searchJobs(
            @RequestParam String keyword) {

        return ResponseEntity.ok(
                jobService.searchJobs(keyword)
        );
    }

    @GetMapping("/api/jobs/filter")
    public ResponseEntity<List<Job>> filterJobsByEligibility(
            @RequestParam Double cgpa,
            @RequestParam String branch) {

        return ResponseEntity.ok(
                jobService.filterJobsByEligibility(cgpa, branch)
        );
    }

    @GetMapping("/api/jobs/search/filter")
    public ResponseEntity<List<Job>> searchAndFilterJobs(
            @RequestParam String keyword,
            @RequestParam Double cgpa,
            @RequestParam String branch) {

        return ResponseEntity.ok(
                jobService.searchAndFilterJobs(keyword, cgpa, branch)
        );
    }

    @GetMapping("/api/jobs/{id}")
    public ResponseEntity<Job> getJobById(@PathVariable Long id) {
        Optional<Job> job = jobService.getJobById(id);

        if (job.isPresent()) {
            return ResponseEntity.ok(job.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/api/jobs/{id}")
    public ResponseEntity<Job> updateJob(
            @PathVariable Long id,
            @RequestBody @Valid Job updatedJob) {

        Job job = jobService.updateJob(id, updatedJob);

        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/api/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {

        boolean deleted = jobService.deleteJob(id);

        if (deleted) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}