# ① Build stage — uses Maven to compile (already done, we use JAR directly)
# We use a lightweight JRE image — not full JDK — for smaller image size
FROM eclipse-temurin:8-jre-jammy

# ② Who maintains this image — good practice to document
LABEL maintainer="aliasghars@gmail.com"

# ③ Create app directory inside container
WORKDIR /app

# ④ Copy the JAR we built into the container
# The JAR name must match exactly what Maven produced
COPY target/discovery-service-0.0.1-SNAPSHOT.jar app.jar

# ⑤ Expose the port this service runs on
EXPOSE 8761

# ⑥ Command to run the JAR when container starts
ENTRYPOINT ["java", "-jar", "app.jar"]