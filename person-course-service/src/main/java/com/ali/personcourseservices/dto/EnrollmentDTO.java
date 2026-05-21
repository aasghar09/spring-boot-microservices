package com.ali.personcourseservices.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a Course Enrollment")
public class EnrollmentDTO {

    @Schema(description = "Unique identifier of the enrollment", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotNull
    @Schema(description = "ID of the person being enrolled", example = "1")
    private Long personId;

    @NotNull
    @Schema(description = "ID of the course to enroll into", example = "2")
    private Long courseId;

    @Schema(description = "Date of enrollment — auto-set by system", example = "2024-01-15", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate enrollmentDate;

    @Schema(description = "Full name of the enrolled person — enriched response field", example = "John Doe", accessMode = Schema.AccessMode.READ_ONLY)
    private String personFullName;

    @Schema(description = "Name of the course — enriched response field", example = "Introduction to Java", accessMode = Schema.AccessMode.READ_ONLY)
    private String courseName;
}