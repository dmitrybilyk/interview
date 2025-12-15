FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY build/libs/designUrlShortenerRedirector-0.0.1-SNAPSHOT.jar /app/redirector.jar
EXPOSE 8085
ENTRYPOINT ["java", "-jar", "/app/redirector.jar"]