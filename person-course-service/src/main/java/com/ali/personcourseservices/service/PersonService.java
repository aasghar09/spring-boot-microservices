package com.ali.personcourseservices.service;

import com.ali.personcourseservices.dto.PersonDTO;
import com.ali.personcourseservices.entity.Person;
import com.ali.personcourseservices.exception.PersonNotFoundException;
import com.ali.personcourseservices.repository.PersonRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// ① @RequiredArgsConstructor — Lombok generates constructor
// for all 'final' fields — this is constructor injection
// Industry best practice over @Autowired field injection
@Service
public class PersonService {

    // ② 'final' + @RequiredArgsConstructor = constructor injection
    // Spring automatically injects PersonRepository here
   @Autowired 
	private PersonRepository personRepository;
    // ③ @Transactional(readOnly=true) — optimizes read operations
    // tells DB: "no writes happening, optimize accordingly"
    @Transactional(readOnly = true)
    public List<PersonDTO> getAllPersons() {
        return personRepository.findAll()
                .stream()
                .map(this::convertToDTO)  // ④ method reference for mapping
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonById(Long id) {
        Person person = personRepository.findById(id)
                // ⑤ If not found — throw custom exception
                // NOT a generic RuntimeException — precise and meaningful
                .orElseThrow(() -> new PersonNotFoundException(
                        "Person not found with id: " + id));
        return convertToDTO(person);
    }

    @Transactional(readOnly = true)
    public PersonDTO getPersonByEmail(String email) {
        Person person = personRepository.findByEmail(email)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Person not found with email: " + email));
        return convertToDTO(person);
    }

    // ⑥ @Transactional without readOnly — enables write operations
    @Transactional
    public PersonDTO createPerson(PersonDTO personDTO) {
        // ⑦ Business rule: no duplicate emails
        if (personRepository.existsByEmail(personDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "Person already exists with email: " + personDTO.getEmail());
        }
        Person person = convertToEntity(personDTO);
        Person savedPerson = personRepository.save(person);
        return convertToDTO(savedPerson);
    }

    @Transactional
    public PersonDTO updatePerson(Long id, PersonDTO personDTO) {
        // ⑧ First verify person exists
        Person existingPerson = personRepository.findById(id)
                .orElseThrow(() -> new PersonNotFoundException(
                        "Person not found with id: " + id));

        // ⑨ Check email uniqueness only if email is being changed
        if (!existingPerson.getEmail().equals(personDTO.getEmail()) &&
                personRepository.existsByEmail(personDTO.getEmail())) {
            throw new IllegalArgumentException(
                    "Email already in use: " + personDTO.getEmail());
        }

        // ⑩ Update fields selectively — never replace the whole entity
        existingPerson.setFirstName(personDTO.getFirstName());
        existingPerson.setLastName(personDTO.getLastName());
        existingPerson.setEmail(personDTO.getEmail());
        existingPerson.setPhone(personDTO.getPhone());

        Person updatedPerson = personRepository.save(existingPerson);
        return convertToDTO(updatedPerson);
    }

    @Transactional
    public void deletePerson(Long id) {
        // ⑪ Verify exists before deleting — give meaningful error if not
        if (!personRepository.existsById(id)) {
            throw new PersonNotFoundException(
                    "Person not found with id: " + id);
        }
        personRepository.deleteById(id);
    }

    // ============================================
    // ⑫ PRIVATE MAPPING METHODS
    // Entity → DTO and DTO → Entity
    // Kept private — only this service needs them
    // ============================================

    private PersonDTO convertToDTO(Person person) {
        PersonDTO dto = new PersonDTO();
        dto.setId(person.getId());
        dto.setFirstName(person.getFirstName());
        dto.setLastName(person.getLastName());
        dto.setEmail(person.getEmail());
        dto.setPhone(person.getPhone());
        return dto;
    }

    private Person convertToEntity(PersonDTO dto) {
        Person person = new Person();
        person.setFirstName(dto.getFirstName());
        person.setLastName(dto.getLastName());
        person.setEmail(dto.getEmail());
        person.setPhone(dto.getPhone());
        return person;
    }
}