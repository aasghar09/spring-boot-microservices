package com.ali.personcourseservices.controller;

import com.ali.personcourseservices.dto.EnrollmentDTO;
import com.ali.personcourseservices.service.EnrollmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/enrollments")
@Validated
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    // GET /api/enrollments
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    // GET /api/enrollments/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(
            @PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    // GET /api/enrollments/person/{personId}
    // Get all courses a specific person is enrolled in
    @GetMapping("/person/{personId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByPerson(
            @PathVariable Long personId) {
        List<EnrollmentDTO> enrollments = enrollmentService
                .getEnrollmentsByPerson(personId);
        return ResponseEntity.ok(enrollments);
    }

    // GET /api/enrollments/course/{courseId}
    // Get all persons enrolled in a specific course
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<EnrollmentDTO>> getEnrollmentsByCourse(
            @PathVariable Long courseId) {
        List<EnrollmentDTO> enrollments = enrollmentService
                .getEnrollmentsByCourse(courseId);
        return ResponseEntity.ok(enrollments);
    }

    // POST /api/enrollments
    // Enroll a person in a course
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EnrollmentDTO> enrollPersonInCourse(
            @Valid @RequestBody EnrollmentDTO enrollmentDTO) {
        EnrollmentDTO created = enrollmentService
                .enrollPersonInCourse(enrollmentDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // DELETE /api/enrollments/{id}
    // Cancel an enrollment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}