package com.placement.placementmanagement.service;

import com.placement.placementmanagement.repository.StudentRepository;
import org.springframework.stereotype.Service;
import com.placement.placementmanagement.entity.Student;
import com.placement.placementmanagement.entity.Student;
import java.util.List;
import java.util.Optional;



    @Service
    public class StudentService {

        private final StudentRepository studentRepository;

        public StudentService(StudentRepository studentRepository) {
            this.studentRepository = studentRepository;
        }

        public Student saveStudent(Student student) {
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

                student.setName(updatedStudent.getName());
                student.setEmail(updatedStudent.getEmail());
                student.setPhone(updatedStudent.getPhone());
                student.setBranch(updatedStudent.getBranch());
                student.setCgpa(updatedStudent.getCgpa());

                return studentRepository.save(student);
            }

            return null;
        }
    }
