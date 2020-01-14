package com.worm.student.controller;

import com.worm.student.pojo.Grade;
import com.worm.student.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/students")
@RequiredArgsConstructor(onConstructor = @_(@Autowired))
public class StudentController {

    private final StudentService studentService;

    @GetMapping("/searchGrade")
    public List<Grade> searchGrade(@RequestParam String studentId, @RequestParam String openingHours) throws Exception {
        return studentService.searchGrade(studentId,openingHours);
    }

}
