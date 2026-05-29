package com.ali.notificationservice.service;

import com.ali.notificationservice.event.EnrollmentEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    public void sendEnrollmentConfirmation(EnrollmentEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(event.getPersonEmail());
        message.setSubject("Enrollment Confirmation — " + event.getCourseName());
        message.setText(
            "Dear " + event.getPersonFullName() + ",\n\n" +
            "You have been successfully enrolled in: " + event.getCourseName() + "\n" +
            "Enrollment Date: " + event.getEnrollmentDate() + "\n" +
            "Enrollment ID: " + event.getEnrollmentId() + "\n\n" +
            "Best regards,\nSchool Administration"
        );
        mailSender.send(message);
    }
}