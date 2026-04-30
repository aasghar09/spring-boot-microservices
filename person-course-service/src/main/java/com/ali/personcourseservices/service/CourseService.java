package com.ali.personcourseservices.service;

import com.ali.personcourseservices.dto.CourseDTO;
import com.ali.personcourseservices.entity.Course;
import com.ali.personcourseservices.exception.CourseNotFoundException;
import com.ali.personcourseservices.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Transactional(readOnly = true)
    public List<CourseDTO> getAllCourses() {
        return courseRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CourseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + id));
        return convertToDTO(course);
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> searchCoursesByName(String name) {
        return courseRepository.findByCourseNameContainingIgnoreCase(name)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<CourseDTO> getCoursesByInstructor(String instructorName) {
        return courseRepository.findByInstructorName(instructorName)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public CourseDTO createCourse(CourseDTO courseDTO) {
        if (courseRepository.existsByCourseName(courseDTO.getCourseName())) {
            throw new IllegalArgumentException(
                    "Course already exists with name: " + courseDTO.getCourseName());
        }
        Course course = convertToEntity(courseDTO);
        Course savedCourse = courseRepository.save(course);
        return convertToDTO(savedCourse);
    }

    @Transactional
    public CourseDTO updateCourse(Long id, CourseDTO courseDTO) {
        Course existingCourse = courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(
                        "Course not found with id: " + id));

        if (!existingCourse.getCourseName().equals(courseDTO.getCourseName()) &&
                courseRepository.existsByCourseName(courseDTO.getCourseName())) {
            throw new IllegalArgumentException(
                    "Course name already in use: " + courseDTO.getCourseName());
        }

        existingCourse.setCourseName(courseDTO.getCourseName());
        existingCourse.setDescription(courseDTO.getDescription());
        existingCourse.setCredits(courseDTO.getCredits());
        existingCourse.setInstructorName(courseDTO.getInstructorName());

        Course updatedCourse = courseRepository.save(existingCourse);
        return convertToDTO(updatedCourse);
    }

    @Transactional
    public void deleteCourse(Long id) {
        if (!courseRepository.existsById(id)) {
            throw new CourseNotFoundException(
                    "Course not found with id: " + id);
        }
        courseRepository.deleteById(id);
    }

    // ============================================
    // PRIVATE MAPPING METHODS
    // ============================================
    private CourseDTO convertToDTO(Course course) {
        CourseDTO dto = new CourseDTO();
        dto.setId(course.getId());
        dto.setCourseName(course.getCourseName());
        dto.setDescription(course.getDescription());
        dto.setCredits(course.getCredits());
        dto.setInstructorName(course.getInstructorName());
        return dto;
    }

    private Course convertToEntity(CourseDTO dto) {
        Course course = new Course();
        course.setCourseName(dto.getCourseName());
        course.setDescription(dto.getDescription());
        course.setCredits(dto.getCredits());
        course.setInstructorName(dto.getInstructorName());
        return course;
    }
}