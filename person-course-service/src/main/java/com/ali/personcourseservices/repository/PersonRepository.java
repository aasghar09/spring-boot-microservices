package com.ali.personcourseservices.repository;

import com.ali.personcourseservices.entity.Person;
import org.springframework.data.jpa.repository.JpaRepository;
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
}