FROM eclipse-temurin:8-jre-jammy

LABEL maintainer="aliasghars@gmail.com"

WORKDIR /app

COPY target/gateway-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "app.jar"]