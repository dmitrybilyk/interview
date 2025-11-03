FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY build/libs/designUrlShortenerGenerator-0.0.1-SNAPSHOT.jar /app/generator.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/generator.jar"]

