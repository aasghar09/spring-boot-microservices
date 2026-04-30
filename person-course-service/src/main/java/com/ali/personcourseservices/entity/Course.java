package com.ali.personcourseservices.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_name", nullable = false)
    private String courseName;

    @Column(name = "description", nullable = false, length = 500)
    private String description;

    @Column(name = "credits", nullable = false)
    private Integer credits;

    @Column(name = "instructor_name", nullable = false)
    private String instructorName;

    @OneToMany(mappedBy = "course",
               cascade = CascadeType.ALL,
               orphanRemoval = true,
               fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();
}