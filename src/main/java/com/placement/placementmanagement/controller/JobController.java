package com.placement.placementmanagement.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.placement.placementmanagement.service.JobService;
import com.placement.placementmanagement.entity.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import java.util.List;
import java.util.Optional;

@RestController
public class JobController {
    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping("/api/jobs")
    public Job createJob(@RequestBody Job job) {
        return jobService.saveJob(job);
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
            @RequestBody Job updatedJob) {

        Job job = jobService.updateJob(id, updatedJob);

        if (job != null) {
            return ResponseEntity.ok(job);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/api/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {

        jobService.deleteJob(id);

        return ResponseEntity.noContent().build();
    }
}
