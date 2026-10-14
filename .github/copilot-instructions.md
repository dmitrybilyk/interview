This is a Java interview preparation project covering: Spring (IoC, AOP, Security, Data JPA, Events, MVC),
OOP, SOLID, design patterns, concurrency, Kafka, reactive programming (Project Reactor),
REST, databases (PostgreSQL, MongoDB, Redis), Kubernetes, gRPC, observability, and LeetCode algorithms.

## Code style
- Java 21. Prefer records over POJOs, switch expressions over switch statements.
- No Lombok unless it already exists in the file.
- No comments explaining WHAT the code does — method and variable names should be self-explanatory.
- Add a comment only when the WHY is non-obvious (a subtle invariant, a workaround, a hidden constraint).

## Project structure
- Demo classes live in `src/main/java/com/conduct/interview/<topic>/<subtopic>/`
- Named `<Concept>Demo.java` or `<Concept>Example.java`
- Each demo has a `main()` method so it can be run standalone without Spring Boot where possible.
- Keep demos minimal — educational clarity over production robustness.

## Build & run
- Build: `./gradlew build -x test`
- Run app: `./gradlew bootRun`
- Run specific class: `./gradlew run --main-class com.conduct.interview.<ClassName>`
- Tests excluded from build — demos require external services (PostgreSQL, Redis, RabbitMQ, Kafka)

## Environment
- Java: `/home/dik81/.jdks/ms-21.0.9/bin/java` (JDK 21, Microsoft build)
- keytool: `/home/dik81/.jdks/ms-21.0.9/bin/keytool`
- Gradle: `./gradlew` (wrapper in project root, no global gradle)
- Docker: `docker`
- Kubernetes: `kubectl`, `helm`
- MongoDB shell: `mongosh`
- Utils: `curl`, `jq`, `openssl`

## Language
- Explain concepts, answer questions, and add explanatory comments in Ukrainian.
