FROM eclipse-temurin:8-jre-jammy

LABEL maintainer="ali@school.com"

WORKDIR /app

COPY target/person-course-service-0.0.1-SNAPSHOT-exec.war app.jar

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "app.jar"]