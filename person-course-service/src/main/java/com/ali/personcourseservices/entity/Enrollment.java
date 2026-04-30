package com.ali.personcourseservices.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;




import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
    name = "enrollment",
    uniqueConstraints = {
        // ① Prevents duplicate enrollment:
        // same person cannot enroll in same course twice
        @UniqueConstraint(
            name = "uk_person_course",
            columnNames = {"person_id", "course_id"}
        )
    }
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ② Many Enrollments → One Person
    // @JoinColumn defines the Foreign Key column in enrollment table
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    private Person person;

    // ③ Many Enrollments → One Course
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // ④ Automatically set enrollment date when record is created
    @Column(name = "enrollment_date", nullable = false)
    private LocalDate enrollmentDate;

    // ⑤ Lifecycle hook — runs BEFORE insert into DB
    @PrePersist
    public void prePersist() {
        if (enrollmentDate == null) {
            enrollmentDate = LocalDate.now();
        }
    }
}