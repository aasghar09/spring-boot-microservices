package com.ali.personcourseservices.controller;

import com.ali.personcourseservices.dto.CourseDTO;
import com.ali.personcourseservices.service.CourseService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Validated
@Tag(name = "Course Management", description = "APIs for managing courses in the school system")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Operation(summary = "Get all courses", description = "Returns a paginated list of all courses. Requires ADMIN or USER role.")
    // GET /api/courses
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<Page<CourseDTO>> getAllCourses(
            @PageableDefault(size = 10, sort = "courseName") Pageable pageable) {
        return ResponseEntity.ok(courseService.getAllCourses(pageable));
    }

    @Operation(summary = "Get course by ID", description = "Returns a single course by its ID. Requires ADMIN or USER role.")
    // GET /api/courses/{id}
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<CourseDTO> getCourseById(@PathVariable Long id) {
        CourseDTO course = courseService.getCourseById(id);
        return ResponseEntity.ok(course);
    }

    @Operation(summary = "Search courses by name", description = "Returns courses matching the name keyword. Requires ADMIN or USER role.")
    // GET /api/courses/search?name=java
    // ① @RequestParam — reads from query string ?name=xxx
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<CourseDTO>> searchCourses(
            @RequestParam String name) {
        List<CourseDTO> courses = courseService.searchCoursesByName(name);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Get courses by instructor", description = "Returns all courses taught by a specific instructor. Requires ADMIN or USER role.")
    // GET /api/courses/instructor/{instructorName}
    @GetMapping("/instructor/{instructorName}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<List<CourseDTO>> getCoursesByInstructor(
            @PathVariable String instructorName) {
        List<CourseDTO> courses = courseService
                .getCoursesByInstructor(instructorName);
        return ResponseEntity.ok(courses);
    }

    @Operation(summary = "Create a new course", description = "Creates a new course record. Requires ADMIN role.")
    // POST /api/courses
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> createCourse(
            @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO created = courseService.createCourse(courseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Update a course", description = "Updates an existing course by ID. Requires ADMIN role.")
    // PUT /api/courses/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        CourseDTO updated = courseService.updateCourse(id, courseDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a course", description = "Deletes a course by ID. Requires ADMIN role.")
    // DELETE /api/courses/{id}
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
}