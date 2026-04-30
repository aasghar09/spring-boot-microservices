package com.ali.personcourseservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    // Exposed on RESPONSE only
    private Long id;

    // ① On REQUEST: caller sends personId + courseId
    // On RESPONSE: we send back full person + course details
    @NotNull(message = "Person ID is required")
    private Long personId;

    @NotNull(message = "Course ID is required")
    private Long courseId;

    // ② Auto-set by @PrePersist — not required on REQUEST
    private LocalDate enrollmentDate;

    // ③ Convenience fields for RESPONSE readability
    // Caller sees name + course name instead of raw IDs
    private String personFullName;
    private String courseName;
}