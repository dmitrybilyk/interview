FROM eclipse-temurin:17-jdk-jammy
WORKDIR /app
COPY target/generator.jar /app/generator.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/generator.jar"]
