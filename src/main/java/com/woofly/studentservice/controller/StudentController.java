package com.woofly.studentservice.controller;

import com.woofly.studentservice.model.Student;
import com.woofly.studentservice.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentRepository studentRepository;

    // Feign Client bu endpoint-ə zəng edəcək
    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Tələbə tapılmadı"));
    }

    // TEST DATA: Proqram işə düşəndə avtomatik 1 tələbə yaratsın
    @Bean
    public CommandLineRunner initData() {
        return args -> {
            studentRepository.save(new Student(null, "Əli", "Vəliyev", "ali@mail.com"));
            System.out.println("TEST DATA: Tələbə (ID: 1) bazaya əlavə edildi.");
        };
    }
}
