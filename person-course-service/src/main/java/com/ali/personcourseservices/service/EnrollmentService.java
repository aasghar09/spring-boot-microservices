package com.ali.personcourseservices.service;

import com.ali.personcourseservices.dto.EnrollmentDTO;
import com.ali.personcourseservices.entity.Course;
import com.ali.personcourseservices.entity.Enrollment;
import com.ali.personcourseservices.entity.Person;
import com.ali.personcourseservices.exception.CourseNotFoundException;
import com.ali.personcourseservices.exception.EnrollmentNotFoundException;
import com.ali.personcourseservices.exception.PersonNotFoundException;
import com.ali.personcourseservices.repository.CourseRepository;
import com.ali.personcourseservices.repository.EnrollmentRepository;
import com.ali.personcourseservices.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EnrollmentService {

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getAllEnrollments() {
        return enrollmentRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public EnrollmentDTO getEnrollmentById(Long id) {
        Enrollment enrollment = enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(
                        "Enrollment not found with id: " + id));
        return convertToDTO(enrollment);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByPerson(Long personId) {
        if (!personRepository.existsById(personId)) {
            throw new PersonNotFoundException(
                    "Person not found with id: " + personId);
        }
        return enrollmentRepository.findByPersonId(personId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<EnrollmentDTO> getEnrollmentsByCourse(Long courseId) {
        if (!courseRepository.existsById(courseId)) {
            throw new CourseNotFoundException(
                    "Course not found with id: " + courseId);
        }
        return enrollmentRepository.findByCourseId(courseId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public EnrollmentDTO enrollPersonInCourse(EnrollmentDTO enrollmentDTO) {

        Person person = personRepository.findById(enrollmentDTO.getPersonId())
                .orElseThrow(() -> new PersonNotFoundException(
                        "Person not found with id: " + enrollmentDTO.getPersonId()));

        Course course = courseRepository.findById(enrollmentDTO.getCourseId())
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + enrollmentDTO.getCourseId()));

        if (enrollmentRepository.existsByPersonIdAndCourseId(
                enrollmentDTO.getPersonId(), enrollmentDTO.getCourseId())) {
            throw new IllegalArgumentException(
                    person.getFirstName() + " " + person.getLastName() +
                    " is already enrolled in " + course.getCourseName());
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setPerson(person);
        enrollment.setCourse(course);

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);
        return convertToDTO(savedEnrollment);
    }

    @Transactional
    public void cancelEnrollment(Long id) {
        if (!enrollmentRepository.existsById(id)) {
            throw new EnrollmentNotFoundException(
                    "Enrollment not found with id: " + id);
        }
        enrollmentRepository.deleteById(id);
    }

    // ============================================
    // PRIVATE MAPPING METHODS
    // ============================================
    private EnrollmentDTO convertToDTO(Enrollment enrollment) {
        EnrollmentDTO dto = new EnrollmentDTO();
        dto.setId(enrollment.getId());
        dto.setPersonId(enrollment.getPerson().getId());
        dto.setCourseId(enrollment.getCourse().getId());
        dto.setEnrollmentDate(enrollment.getEnrollmentDate());
        dto.setPersonFullName(
                enrollment.getPerson().getFirstName() + " " +
                enrollment.getPerson().getLastName());
        dto.setCourseName(enrollment.getCourse().getCourseName());
        return dto;
    }
}