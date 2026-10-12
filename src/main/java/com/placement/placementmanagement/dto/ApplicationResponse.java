package com.placement.placementmanagement.dto;

import com.placement.placementmanagement.entity.Application;
import com.placement.placementmanagement.entity.ApplicationStatus;

import java.time.LocalDate;

/**
 * What the API returns for an application.
 * Deliberately leaves out the student's email and phone number.
 */
public record ApplicationResponse(
        Long id,
        LocalDate appliedDate,
        ApplicationStatus status,
        Long studentId,
        String studentName,
        String studentBranch,
        Long jobId,
        String jobTitle,
        String company
) {

    public static ApplicationResponse from(Application application) {
        return new ApplicationResponse(
                application.getId(),
                application.getAppliedDate(),
                application.getStatus(),
                application.getStudent().getId(),
                application.getStudent().getName(),
                application.getStudent().getBranch(),
                application.getJob().getId(),
                application.getJob().getTitle(),
                application.getJob().getCompany()
        );
    }
}