package com.ali.personcourseservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PersonCourseServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(PersonCourseServiceApplication.class, args);
    }
}