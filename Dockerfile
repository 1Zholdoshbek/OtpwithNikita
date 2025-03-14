FROM ubuntu:latest
LABEL authors="tashiev"

ENTRYPOINT ["top", "-b"]

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the project's JAR file to the container
COPY target/OTPwithNikita-0.0.1-SNAPSHOT.jar app.jar

# Expose the port your Spring Boot app uses
EXPOSE 7007

# Run the JAR file
ENTRYPOINT ["java", "-jar", "app.jar"]