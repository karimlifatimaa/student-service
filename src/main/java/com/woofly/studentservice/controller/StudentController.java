package com.woofly.studentservice.controller;

import com.woofly.studentservice.model.Student;
import com.woofly.studentservice.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentService.createStudent(student);
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentService.getStudentById(id);
    }

    @GetMapping("/course/{courseId}")
    public List<Student> getStudentsByCourse(@PathVariable Long courseId) {
        return studentService.getStudentsByCourseId(courseId);
    }
}
