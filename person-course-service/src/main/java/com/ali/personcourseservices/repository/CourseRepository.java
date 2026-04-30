package com.ali.personcourseservices.repository;

import com.ali.personcourseservices.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    // ① Find courses by instructor
    // SELECT * FROM course WHERE instructor_name = ?
    List<Course> findByInstructorName(String instructorName);

    // ② Case-insensitive course name search
    // SELECT * FROM course WHERE LOWER(course_name) LIKE LOWER(?)
    List<Course> findByCourseNameContainingIgnoreCase(String courseName);

    // ③ Check if course name already exists
    boolean existsByCourseName(String courseName);
}