package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EligibilityServiceTest {

    // The check methods never touch the repositories, so null is fine here.
    private final EligibilityService service = new EligibilityService(null, null);

    private Student student(String branch, Double cgpa, String skills, Integer backlogs) {
        Student s = new Student();
        s.setBranch(branch);
        s.setCgpa(cgpa);
        s.setSkills(skills);
        s.setBacklogs(backlogs);
        return s;
    }

    private Job job(String branches, Double minCgpa, String skills, Integer maxBacklogs) {
        Job j = new Job();
        j.setEligibleBranches(branches);
        j.setMinimumCgpa(minCgpa);
        j.setRequiredSkills(skills);
        j.setMaximumBacklogs(maxBacklogs);
        return j;
    }

    // ---------- CGPA ----------

    @Test
    void cgpa_equalToMinimum_passes() {
        assertTrue(service.checkCgpa(
                student("CSE", 7.0, "java", 0),
                job("CSE", 7.0, "java", 0)));
    }

    @Test
    void cgpa_aboveMinimum_passes() {
        assertTrue(service.checkCgpa(
                student("CSE", 8.5, "java", 0),
                job("CSE", 7.0, "java", 0)));
    }

    @Test
    void cgpa_belowMinimum_fails() {
        assertFalse(service.checkCgpa(
                student("CSE", 6.9, "java", 0),
                job("CSE", 7.0, "java", 0)));
    }

    @Test
    void cgpa_nullValues_failInsteadOfCrashing() {
        assertFalse(service.checkCgpa(
                student("CSE", null, "java", 0),
                job("CSE", 7.0, "java", 0)));
        assertFalse(service.checkCgpa(
                student("CSE", 8.0, "java", 0),
                job("CSE", null, "java", 0)));
    }

    // ---------- Branch ----------

    @Test
    void branch_listedBranch_passes() {
        assertTrue(service.checkBranch(
                student("CSE", 8.0, "java", 0),
                job("CSE,IT,ECE", 7.0, "java", 0)));
    }

    @Test
    void branch_ignoresCaseAndSpaces() {
        assertTrue(service.checkBranch(
                student("  cse ", 8.0, "java", 0),
                job("IT,  CSE , ECE", 7.0, "java", 0)));
    }

    @Test
    void branch_notListed_fails() {
        assertFalse(service.checkBranch(
                student("MECH", 8.0, "java", 0),
                job("CSE,IT", 7.0, "java", 0)));
    }

    @Test
    void branch_nullValues_fail() {
        assertFalse(service.checkBranch(
                student(null, 8.0, "java", 0),
                job("CSE", 7.0, "java", 0)));
        assertFalse(service.checkBranch(
                student("CSE", 8.0, "java", 0),
                job(null, 7.0, "java", 0)));
    }

    // ---------- Skills ----------

    @Test
    void skills_allRequiredPresent_passes() {
        assertTrue(service.checkSkills(
                student("CSE", 8.0, "Java, Spring Boot, SQL", 0),
                job("CSE", 7.0, "java,sql", 0)));
    }

    @Test
    void skills_extraSkillsAreFine() {
        assertTrue(service.checkSkills(
                student("CSE", 8.0, "java,python,docker", 0),
                job("CSE", 7.0, "java", 0)));
    }

    @Test
    void skills_oneMissing_fails() {
        assertFalse(service.checkSkills(
                student("CSE", 8.0, "java", 0),
                job("CSE", 7.0, "java,sql", 0)));
    }

    @Test
    void skills_nullValues_fail() {
        assertFalse(service.checkSkills(
                student("CSE", 8.0, null, 0),
                job("CSE", 7.0, "java", 0)));
        assertFalse(service.checkSkills(
                student("CSE", 8.0, "java", 0),
                job("CSE", 7.0, null, 0)));
    }

    // ---------- Backlogs ----------

    @Test
    void backlogs_equalToMaximum_passes() {
        assertTrue(service.checkBacklogs(
                student("CSE", 8.0, "java", 2),
                job("CSE", 7.0, "java", 2)));
    }

    @Test
    void backlogs_aboveMaximum_fails() {
        assertFalse(service.checkBacklogs(
                student("CSE", 8.0, "java", 3),
                job("CSE", 7.0, "java", 2)));
    }

    @Test
    void backlogs_noBacklogs_passesWhenZeroAllowed() {
        assertTrue(service.checkBacklogs(
                student("CSE", 8.0, "java", 0),
                job("CSE", 7.0, "java", 0)));
    }

    @Test
    void backlogs_nullValues_fail() {
        assertFalse(service.checkBacklogs(
                student("CSE", 8.0, "java", null),
                job("CSE", 7.0, "java", 0)));
        assertFalse(service.checkBacklogs(
                student("CSE", 8.0, "java", 0),
                job("CSE", 7.0, "java", null)));
    }

    // ---------- Overall eligibility ----------

    @Test
    void isEligible_whenEverythingMatches_isTrue() {
        assertTrue(service.isEligible(
                student("CSE", 8.0, "java,sql", 0),
                job("CSE,IT", 7.0, "java", 1)));
    }

    @Test
    void isEligible_whenCgpaTooLow_isFalse() {
        assertFalse(service.isEligible(
                student("CSE", 6.0, "java,sql", 0),
                job("CSE,IT", 7.0, "java", 1)));
    }

    @Test
    void isEligible_whenBranchNotAllowed_isFalse() {
        assertFalse(service.isEligible(
                student("MECH", 8.0, "java,sql", 0),
                job("CSE,IT", 7.0, "java", 1)));
    }

    @Test
    void isEligible_whenSkillMissingOrTooManyBacklogs_isFalse() {
        assertFalse(service.isEligible(
                student("CSE", 8.0, "python", 0),
                job("CSE,IT", 7.0, "java", 1)));
        assertFalse(service.isEligible(
                student("CSE", 8.0, "java", 5),
                job("CSE,IT", 7.0, "java", 1)));
    }
}