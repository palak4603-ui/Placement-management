
package com.placement.placementmanagement.controller;

import com.placement.placementmanagement.dto.ApplicationResponse;
import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.ApplicationStatus;
import com.placement.placementmanagement.service.ApplicationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final Long configuredStudentId;

    public ApplicationController(
            ApplicationService applicationService,
            @Value("${app.security.student-id:2}") Long configuredStudentId) {
        this.applicationService = applicationService;
        this.configuredStudentId = configuredStudentId;
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN"));
    }

    private void verifyStudentAccess(
            Authentication authentication,
            Long requestedStudentId) {

        if (!isAdmin(authentication)
                && !configuredStudentId.equals(requestedStudentId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You can access only your own applications");
        }
    }

    @PostMapping
    public ResponseEntity<ApplicationResponse> applyForJob(
            @RequestParam(required = false) Long studentId,
            @RequestParam Long jobId,
            Authentication authentication) {

        Long actualStudentId;

        if (isAdmin(authentication)) {
            if (studentId == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Student ID is required for admin requests");
            }

            actualStudentId = studentId;
        } else {
            // Never trust a student ID supplied by the client
            actualStudentId = configuredStudentId;
        }

        Application application =
                applicationService.applyForJob(actualStudentId, jobId);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApplicationResponse.from(application));
    }

    @GetMapping("/{applicationId}")
    public ResponseEntity<ApplicationResponse> getApplicationById(
            @PathVariable Long applicationId,
            Authentication authentication) {

        Application application =
                applicationService.getApplicationById(applicationId);

        verifyStudentAccess(
                authentication,
                application.getStudent().getId());

        return ResponseEntity.ok(
                ApplicationResponse.from(application));
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<ApplicationResponse>> getApplicationsByStudent(
            @PathVariable Long studentId,
            Authentication authentication) {

        verifyStudentAccess(authentication, studentId);

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
                        applicationService.updateStatus(
                                applicationId, status))
        );
    }

    @DeleteMapping("/{applicationId}")
    public ResponseEntity<Void> deleteApplication(
            @PathVariable Long applicationId) {

        applicationService.deleteApplication(applicationId);

        return ResponseEntity.noContent().build();
    }
}
