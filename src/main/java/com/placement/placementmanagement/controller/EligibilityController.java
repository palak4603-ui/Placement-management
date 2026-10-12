package com.placement.placementmanagement.controller;

import com.placement.placementmanagement.dto.EligibilityResponse;
import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.service.EligibilityService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EligibilityController {

    private final EligibilityService eligibilityService;

    public EligibilityController(EligibilityService eligibilityService) {
        this.eligibilityService = eligibilityService;
    }
    @GetMapping("/api/eligibility/student/{studentId}/job/{jobId}")

    public EligibilityResponse checkEligibility(
            @PathVariable Long studentId,
            @PathVariable Long jobId) {

        Student student = eligibilityService.getStudent(studentId);
        Job job = eligibilityService.getJob(jobId);

        boolean cgpaEligible = eligibilityService.checkCgpa(student, job);
        boolean branchEligible = eligibilityService.checkBranch(student, job);
        boolean skillsEligible = eligibilityService.checkSkills(student, job);
        boolean backlogsEligible = eligibilityService.checkBacklogs(student, job);

        boolean eligible = cgpaEligible
                && branchEligible
                && skillsEligible
                && backlogsEligible;

        return new EligibilityResponse(
                eligible,
                cgpaEligible,
                branchEligible,
                skillsEligible,
                backlogsEligible
        );
    }
}