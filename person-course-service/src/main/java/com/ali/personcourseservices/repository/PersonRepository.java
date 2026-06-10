package com.ali.personcourseservices.repository;

import com.ali.personcourseservices.entity.Person;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository  // ① Marks this as a Spring-managed repository bean
public interface PersonRepository extends JpaRepository<Person, Long> {

    // ② Spring generates the SQL automatically from the method name
    // SELECT * FROM person WHERE email = ?
    Optional<Person> findByEmail(String email);

    // ③ Check if email already exists — useful for duplicate validation
    // SELECT COUNT(*) > 0 FROM person WHERE email = ?
    boolean existsByEmail(String email);
    
    
 // Search by firstName containing keyword OR email containing keyword
 // Spring Data JPA generates the SQL automatically from the method name
 // IMPORTANT: returns Page<Person> — keeps pagination working with search
 @Query("SELECT p FROM Person p WHERE " +
        "LOWER(p.firstName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(p.lastName)  LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
        "LOWER(p.email)     LIKE LOWER(CONCAT('%', :keyword, '%'))")
 Page<Person> searchPersons(@Param("keyword") String keyword, Pageable pageable);
}