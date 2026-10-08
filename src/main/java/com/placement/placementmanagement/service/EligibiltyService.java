package com.placement.placementmanagement.service;
import com.placement.placementmanagement.entity.Job;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.JobRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;


@Service
public class EligibiltyService {
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;


    public EligibiltyService(StudentRepository studentRepository, JobRepository jobRepository) {
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }
    public boolean checkCgpa(Student student, Job job) {
        return student.getCgpa() >= job.getMinimumCgpa();
    }
    public boolean checkBranch(Student student, Job job) {
        return job.getEligibleBranches().contains(student.getBranch());
    }
    public boolean checkSkills(Student student, Job job) {

        String[] requiredSkills = job.getRequiredSkills().split(",");

        for (String skill : requiredSkills) {

            if (!student.getSkills().toLowerCase().contains(skill.trim().toLowerCase())) {
                return false;
            }
        }

        return true;
    }
    public boolean checkBacklogs(Student student, Job job) {
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
                .orElseThrow(() -> new RuntimeException("Student not found"));
    }
    public Job getJob(Long jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
    }
}
