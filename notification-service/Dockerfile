FROM eclipse-temurin:8-jre-jammy
LABEL maintainer="ali@school.com"
WORKDIR /app
COPY target/notification-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java", "-jar", "app.jar"]