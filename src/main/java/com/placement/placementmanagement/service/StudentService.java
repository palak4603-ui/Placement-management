package com.placement.placementmanagement.service;

import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.repository.ApplicationRepository;
import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    private final StudentRepository studentRepository;
    private final ApplicationRepository applicationRepository;

    public StudentService(StudentRepository studentRepository,
                          ApplicationRepository applicationRepository) {
        this.studentRepository = studentRepository;
        this.applicationRepository = applicationRepository;
    }

    private String normalizeEmail(String email) {
        return email == null ? null : email.trim().toLowerCase();
    }

    public Student saveStudent(Student student) {

        // A POST must always create a new student, never overwrite one
        student.setId(null);

        student.setEmail(normalizeEmail(student.getEmail()));

        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A student with this email already exists"
            );
        }

        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Optional<Student> getStudentById(Long id) {
        return studentRepository.findById(id);
    }

    public Student updateStudent(Long id, Student updatedStudent) {
        Optional<Student> existingStudent = studentRepository.findById(id);

        if (existingStudent.isPresent()) {
            Student student = existingStudent.get();

            String email = normalizeEmail(updatedStudent.getEmail());

            if (studentRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Another student already uses this email"
                );
            }

            student.setName(updatedStudent.getName());
            student.setEmail(email);
            student.setPhone(updatedStudent.getPhone());
            student.setBranch(updatedStudent.getBranch());
            student.setCgpa(updatedStudent.getCgpa());
            student.setSkills(updatedStudent.getSkills());
            student.setBacklogs(updatedStudent.getBacklogs());

            return studentRepository.save(student);
        }

        return null;
    }

    public boolean deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            return false;
        }

        if (applicationRepository.existsByStudentId(id)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete a student who has job applications. "
                            + "Delete the applications first."
            );
        }

        studentRepository.deleteById(id);
        return true;
    }
}