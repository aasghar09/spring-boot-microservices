package com.ali.personcourseservices.repository;

import com.ali.personcourseservices.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    // ① All enrollments for a specific person
    List<Enrollment> findByPersonId(Long personId);

    // ② All enrollments for a specific course
    List<Enrollment> findByCourseId(Long courseId);

    // ③ Find a specific enrollment by person AND course
    // Used to check duplicate enrollment before saving
    Optional<Enrollment> findByPersonIdAndCourseId(Long personId, Long courseId);

    // ④ Check if enrollment already exists
    boolean existsByPersonIdAndCourseId(Long personId, Long courseId);

    // ⑤ Custom JPQL query — counts enrollments per course
    // JPQL uses Entity class names and field names — NOT table/column names
    @Query("SELECT COUNT(e) FROM Enrollment e WHERE e.course.id = :courseId")
    Long countEnrollmentsByCourseId(@Param("courseId") Long courseId);
}