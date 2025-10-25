FROM eclipse-temurin:21-jdk

WORKDIR /app

# Copy your prebuilt JAR from host machine into image
COPY build/libs/SystemDesign-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
