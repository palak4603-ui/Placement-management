package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.JobRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class EligibilityService {

    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public EligibilityService(
            StudentRepository studentRepository,
            JobRepository jobRepository) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    public boolean checkCgpa(Student student, Job job) {
        if (student.getCgpa() == null || job.getMinimumCgpa() == null) {
            return false;
        }
        return student.getCgpa() >= job.getMinimumCgpa();
    }

    public boolean checkBranch(Student student, Job job) {
        if (student.getBranch() == null
                || job.getEligibleBranches() == null) {
            return false;
        }

        String studentBranch = student.getBranch().trim();

        return Arrays.stream(job.getEligibleBranches().split(","))
                .map(String::trim)
                .anyMatch(branch ->
                        branch.equalsIgnoreCase(studentBranch));
    }

    public boolean checkSkills(Student student, Job job) {
        if (student.getSkills() == null
                || job.getRequiredSkills() == null) {
            return false;
        }

        Set<String> studentSkills = Arrays.stream(
                        student.getSkills().split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());

        String[] requiredSkills = job.getRequiredSkills().split(",");

        for (String skill : requiredSkills) {
            if (!studentSkills.contains(skill.trim().toLowerCase())) {
                return false;
            }
        }

        return true;
    }

    public boolean checkBacklogs(Student student, Job job) {
        if (student.getBacklogs() == null || job.getMaximumBacklogs() == null) {
            return false;
        }
        return student.getBacklogs() <= job.getMaximumBacklogs();
    }

    public boolean isEligible(Student student, Job job) {
        return checkCgpa(student, job)
                && checkBranch(student, job)
                && checkSkills(student, job)
                && checkBacklogs(student, job);
    }

    public Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student not found"));
    }

    public Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Job not found"));
    }
}