package com.ali.personcourseservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a Course")
public class CourseDTO {

    // Exposed on RESPONSE only
	@Schema(description = "Unique identifier of the course", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @NotBlank(message = "Course name is required")
    @Size(min = 2, max = 100, message = "Course name must be between 2 and 100 characters")
    @Schema(description = "Name of the course", example = "Introduction to Java")
    private String courseName;

    @NotBlank(message = "Description is required")
    @Schema(description = "Number of credit hours", example = "3")
    private String description;

    @Min(value = 1, message = "Credits must be at least 1")
    @Schema(description = "Number of credit hours", example = "3")
    private Integer credits;

    @NotBlank(message = "Instructor name is required")
    @Schema(description = "Full name of the instructor", example = "Dr. Smith")
    private String instructorName;
}