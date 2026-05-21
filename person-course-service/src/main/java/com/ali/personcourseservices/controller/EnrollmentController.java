package com.ali.personcourseservices.controller;

import com.ali.personcourseservices.dto.EnrollmentDTO;
import com.ali.personcourseservices.service.EnrollmentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

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
@Tag(name = "Enrollment Management", description = "APIs for managing course enrollments")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Operation(summary = "Get all enrollments", description = "Returns a list of all enrollments. Requires ADMIN role.")
    // GET /api/enrollments
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentDTO>> getAllEnrollments() {
        List<EnrollmentDTO> enrollments = enrollmentService.getAllEnrollments();
        return ResponseEntity.ok(enrollments);
    }

    
    @Operation(summary = "Get enrollment by ID", description = "Returns a single enrollment by its ID. Requires ADMIN or USER role.")
    // GET /api/enrollments/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<EnrollmentDTO> getEnrollmentById(
            @PathVariable Long id) {
        EnrollmentDTO enrollment = enrollmentService.getEnrollmentById(id);
        return ResponseEntity.ok(enrollment);
    }

    @Operation(summary = "Get enrollments by person", description = "Returns all enrollments for a specific person. Requires ADMIN or USER role.")
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

    @Operation(summary = "Get enrollments by course", description = "Returns all enrollments for a specific course. Requires ADMIN or USER role.")
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

    @Operation(summary = "Enroll person in course", description = "Enroll a person in a course")
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

    @Operation(summary = "Cancel an enrollment", description = "Removes an enrollment by ID. Requires ADMIN role.")
    // DELETE /api/enrollments/{id}
    // Cancel an enrollment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> cancelEnrollment(@PathVariable Long id) {
        enrollmentService.cancelEnrollment(id);
        return ResponseEntity.noContent().build();
    }
}