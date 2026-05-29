package com.ali.notificationservice.event;

import java.io.Serializable;
import java.time.LocalDate;

public class EnrollmentEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long enrollmentId;
    private Long personId;
    private String personFullName;
    private String personEmail;
    private Long courseId;
    private String courseName;
    private LocalDate enrollmentDate;

    // No-args constructor — required for JSON deserialization
    public EnrollmentEvent() {}

    public EnrollmentEvent(Long enrollmentId, Long personId, String personFullName,
                           String personEmail, Long courseId, String courseName,
                           LocalDate enrollmentDate) {
        this.enrollmentId   = enrollmentId;
        this.personId       = personId;
        this.personFullName = personFullName;
        this.personEmail    = personEmail;
        this.courseId       = courseId;
        this.courseName     = courseName;
        this.enrollmentDate = enrollmentDate;
    }

    // Getters and Setters (no Lombok — avoid any @Data issues on events)
    public Long getEnrollmentId()          { return enrollmentId; }
    public void setEnrollmentId(Long id)   { this.enrollmentId = id; }

    public Long getPersonId()              { return personId; }
    public void setPersonId(Long id)       { this.personId = id; }

    public String getPersonFullName()      { return personFullName; }
    public void setPersonFullName(String n){ this.personFullName = n; }

    public String getPersonEmail()         { return personEmail; }
    public void setPersonEmail(String e)   { this.personEmail = e; }

    public Long getCourseId()              { return courseId; }
    public void setCourseId(Long id)       { this.courseId = id; }

    public String getCourseName()          { return courseName; }
    public void setCourseName(String n)    { this.courseName = n; }

    public LocalDate getEnrollmentDate()         { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate d)   { this.enrollmentDate = d; }
}