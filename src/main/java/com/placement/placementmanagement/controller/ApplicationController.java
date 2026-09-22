package com.placement.placementmanagement.controller;

import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<Application> applyForJob(
            @RequestParam Long studentId,
            @RequestParam Long jobId) {

        Application application =
                applicationService.applyForJob(studentId, jobId);

        return ResponseEntity.ok(application);
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<Application>> getApplicationsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByStudent(studentId)
        );
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<Application>> getApplicationsByJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId)
        );
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<Application> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam String status) {

        return ResponseEntity.ok(
                applicationService.updateStatus(applicationId, status)
        );
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<String> deleteApplication(
            @PathVariable Long applicationId) {

        applicationService.deleteApplication(applicationId);

        return ResponseEntity.ok("Application deleted successfully");
    }
}