package com.ali.personcourseservices.controller;

import com.ali.personcourseservices.dto.PersonDTO;
import com.ali.personcourseservices.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class PersonController {

    @Autowired
    private PersonService personService;

    // ④ GET /api/persons
    // Returns all persons — ADMIN only
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<PersonDTO>> getAllPersons() {
        List<PersonDTO> persons = personService.getAllPersons();
        return ResponseEntity.ok(persons); // ⑤ 200 OK
    }

    // GET /api/persons/{id}
    // Any authenticated user can fetch by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonDTO> getPersonById(@PathVariable Long id) {
        PersonDTO person = personService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    // GET /api/persons/email/{email}
    @GetMapping("/email/{email}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<PersonDTO> getPersonByEmail(
            @PathVariable String email) {
        PersonDTO person = personService.getPersonByEmail(email);
        return ResponseEntity.ok(person);
    }

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

    // PUT /api/persons/{id}
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PersonDTO> updatePerson(
            @PathVariable Long id,
            @Valid @RequestBody PersonDTO personDTO) {
        PersonDTO updated = personService.updatePerson(id, personDTO);
        return ResponseEntity.ok(updated);
    }

    // ⑧ DELETE /api/persons/{id}
    // Returns 204 NO CONTENT — successful delete has no body
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePerson(@PathVariable Long id) {
        personService.deletePerson(id);
        return ResponseEntity.noContent().build(); // ⑨ 204 NO CONTENT
    }
}