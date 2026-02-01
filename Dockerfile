# Stage 1: Build the application
FROM maven:3.8-eclipse-temurin-11 AS builder

WORKDIR /app

# Copy pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application
# We skip tests to speed up the build in CI, but you can remove -DskipTests if you have tests
RUN mvn clean package -DskipTests

# Stage 2: Create the runtime image
FROM eclipse-temurin:11-jre

WORKDIR /app

# Install FFmpeg (required for jaffree)
RUN apt-get update && \
    apt-get install -y ffmpeg && \
    rm -rf /var/lib/apt/lists/*

# Copy the built jar from the builder stage
# Note: The jar name is constructed from artifactId-version-jar-with-dependencies.jar
COPY --from=builder /app/target/RicoFilm2-0.0.1-SNAPSHOT-jar-with-dependencies.jar app.jar

# Expose the port the app runs on (prop CONFIG REQUEST_HTTP_PORT=3000)
EXPOSE 3000

# Set volume for logs if needed (optional)
# VOLUME /app/logs

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

# Default action if none provided (can be overridden by CMD or passing args to docker run)
CMD ["AJOUT_FILM"]
