package com.placement.placementmanagement.controller;

import com.placement.placementmanagement.dto.ApplicationResponse;
import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.ApplicationStatus;
import com.placement.placementmanagement.service.ApplicationService;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<ApplicationResponse> applyForJob(
            @RequestParam Long studentId,
            @RequestParam Long jobId) {

        Application application =
                applicationService.applyForJob(studentId, jobId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApplicationResponse.from(application));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Long applicationId) {

        return ResponseEntity.ok(
                ApplicationResponse.from(
                        applicationService.getApplicationById(applicationId))
        );
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStudent(
            @PathVariable Long studentId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByStudent(studentId)
                        .stream()
                        .map(ApplicationResponse::from)
                        .toList()
        );
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByJob(
            @PathVariable Long jobId) {

        return ResponseEntity.ok(
                applicationService.getApplicationsByJob(jobId)
                        .stream()
                        .map(ApplicationResponse::from)
                        .toList()
        );
    }

    @PutMapping("/{applicationId}/status")
    public ResponseEntity<ApplicationResponse> updateStatus(
            @PathVariable Long applicationId,
            @RequestParam ApplicationStatus status) {

        return ResponseEntity.ok(
                ApplicationResponse.from(
                        applicationService.updateStatus(applicationId, status))
        );
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long applicationId) {

        applicationService.deleteApplication(applicationId);

        return ResponseEntity.noContent().build();
    }
}