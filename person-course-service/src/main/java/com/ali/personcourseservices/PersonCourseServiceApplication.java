package com.ali.personcourseservices;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class PersonCourseServiceApplication
        extends SpringBootServletInitializer {  // ADD this

    // ADD this method — entry point for external servers
    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.sources(
                PersonCourseServiceApplication.class);
    }

    // This stays exactly as before — entry point for JAR
    public static void main(String[] args) {
        SpringApplication.run(
                PersonCourseServiceApplication.class, args);
    }
}