package com.worm.student.service;

import com.worm.student.pojo.Grade;

import java.util.List;

public interface StudentService {

    void searchTimetable(String studentId, String openingHours, Integer week);

    List<Grade> searchGrade(String studentId, String openingHours) throws Exception;

}
