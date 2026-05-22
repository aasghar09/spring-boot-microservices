package com.ali.personcourseservices.controller;

import com.ali.personcourseservices.dto.PersonDTO;
import com.ali.personcourseservices.service.PersonService;

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

// ① @RestController = @Controller + @ResponseBody
// Every method automatically serializes return value to JSON
@RestController

// ② Base URL for all endpoints in this controller
@RequestMapping("/api/persons")

// ③ @Validated enables method-level validation
@Validated
@Tag(name="Person Managemnt", description="APIs for managing persons in the school system")
public class PersonController {

    @Autowired
    private PersonService personService;

    // ④ GET /api/persons
    // Returns all persons — ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all persons", description = "Returns a paginated list of all persons. Requires ADMIN role.")
    public ResponseEntity<Page<PersonDTO>> getAllPersons(
            @PageableDefault(size = 10, sort = "firstName") Pageable pageable) {
        return ResponseEntity.ok(personService.getAllPersons(pageable));
    }

    @Operation(summary = "Get person by ID", description = "Returns a single person by their ID. Requires ADMIN or USER role.")
    // GET /api/persons/{id}
    // Any authenticated user can fetch by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        PersonDTO person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    @Operation(summary = "Get person by email", description = "Returns a single person by their email address. Requires ADMIN or USER role.")
    // GET /api/persons/email/{email}
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonDTO> getPersonByEmail(
            @PathVariable String email) {
        PersonDTO person = personService.getPersonByEmail(email);
        return ResponseEntity.ok(person);
    }

    @Operation(summary = "Create a new person", description = "Creates a new person record. Requires ADMIN role.")
    // ⑥ POST /api/persons
    // @Valid triggers DTO validation annotations
    // @RequestBody deserializes incoming JSON to PersonDTO
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDTO> createPerson(
            @Valid @RequestBody PersonDTO personDTO) {
        PersonDTO created = personService.createPerson(personDTO);
        // ⑦ 201 CREATED — more precise than 200 for resource creation
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @Operation(summary = "Update a person", description = "Updates an existing person by ID. Requires ADMIN role.")
    // PUT /api/persons/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDTO> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonDTO personDTO) {
        PersonDTO updated = personService.updatePerson(id, personDTO);
        return ResponseEntity.ok(updated);
    }

    @Operation(summary = "Delete a person", description = "Deletes a person by ID. Requires ADMIN role.")
    // ⑧ DELETE /api/persons/{id}
    // Returns 204 NO CONTENT — successful delete has no body
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build(); // ⑨ 204 NO CONTENT
    }
}