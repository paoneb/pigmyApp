# Use an official OpenJDK runtime as base image
FROM eclipse-temurin:21-jdk

# Set working directory inside container
WORKDIR /app

# Copy JAR file into container
COPY target/pigmyApp-0.0.1-SNAPSHOT.jar app.jar
# Run the application
EXPOSE 1002

ENTRYPOINT ["java", "-jar", "app.jar"]