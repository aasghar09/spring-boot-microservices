package com.ali.notificationservice.consumer;

import com.ali.notificationservice.event.EnrollmentEvent;
import com.ali.notificationservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class EnrollmentConsumer {

    private static final Logger log = LoggerFactory.getLogger(EnrollmentConsumer.class);

    @Autowired
    private EmailService emailService;

    @KafkaListener(
        topics = "enrollment-events",
        groupId = "notification-group"
    )
    public void handleEnrollmentEvent(EnrollmentEvent event) {
        log.info("Received enrollment event for: {} — course: {}",
                 event.getPersonFullName(), event.getCourseName());
        try {
            emailService.sendEnrollmentConfirmation(event);
            log.info("Email sent successfully to: {}", event.getPersonEmail());
        } catch (Exception e) {
            log.error("Failed to send email for enrollment {}: {}",
                      event.getEnrollmentId(), e.getMessage());
        }
    }
}