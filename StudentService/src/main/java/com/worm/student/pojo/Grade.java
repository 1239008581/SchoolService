package com.worm.student.pojo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Grade {

    private String studentId;

    private String studentName;

    private String openingHours;

    private String courseName;

    private String courseGrade;

    private String courseNature;

    private String courseCategory;

    private String coursePeriod;

    private String courseCredits;

    private String examNature;
}
