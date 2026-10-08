package com.placement.placementmanagement.dto;

public class EligibilityResponse {
    private boolean eligible;
    private boolean cgpaEligible;
    private boolean branchEligible;
    private boolean skillsEligible;
    private boolean backlogsEligible;
    public EligibilityResponse(boolean eligible,
                               boolean cgpaEligible,
                               boolean branchEligible,
                               boolean skillsEligible,
                               boolean backlogsEligible) {

        this.eligible = eligible;
        this.cgpaEligible = cgpaEligible;
        this.branchEligible = branchEligible;
        this.skillsEligible = skillsEligible;
        this.backlogsEligible = backlogsEligible;
    }
    public boolean isEligible() {
        return eligible;
    }

    public boolean isCgpaEligible() {
        return cgpaEligible;
    }

    public boolean isBranchEligible() {
        return branchEligible;
    }

    public boolean isSkillsEligible() {
        return skillsEligible;
    }

    public boolean isBacklogsEligible() {
        return backlogsEligible;
    }
}
