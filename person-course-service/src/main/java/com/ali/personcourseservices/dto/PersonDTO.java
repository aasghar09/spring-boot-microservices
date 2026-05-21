package com.ali.personcourseservices.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.v3.oas.annotations.media.Schema;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data Transfer Object representing a Person")
public class PersonDTO {

    @Schema(description = "Unique identifier of the person", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
	private Long id;

    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Schema(description = "First name of the person", example = "John")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Schema(description = "Last name of the person", example = "Doe")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Schema(description = "Email address — must be unique", example = "john.doe@school.com")
    private String email;
    
    @Schema(description = "Contact phone number", example = "+92-300-1234567")
    private String phone;
    
    
}