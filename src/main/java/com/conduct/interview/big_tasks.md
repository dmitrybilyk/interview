✅ 1. Spring Boot 2 → 3 + JDK 17 → 21 Upgrade
🛠️ What I did:
I upgraded the platform from Spring Boot 2 to 3, and JDK from 17 to 21, to improve performance, get long-term support, 
and adopt new features.

⚠️ Main issues I had to address:
✅ Spring Boot 3 requires Jakarta packages (javax.* → jakarta.*)

Refactoring annotations and imports like @Transactional, @PathParam, etc.

✅ Removed deprecated features (e.g., WebSecurityConfigurerAdapter)

✅ Library compatibility

Many third-party libraries had to be updated or replaced

✅ Test failures due to Spring behavior changes

Fixed tests that depended on internal boot lifecycle or mocks

✅ 2. Rewriting Monolith into Microservices (Kotlin, Spring Boot, Monorepo)
🛠️ What I did:
I helped break down a large monolithic Java application into Kotlin Spring Boot microservices
within a monorepo structure. APIs were defined using OpenAPI and shared across services.

⚙️ How I approached it:
✅ Identified bounded contexts and grouped related features into service domains

✅ Defined OpenAPI specs per service and generated API interfaces using OpenAPI Generator

Ensured strict contract between services

✅ Separated APIs into reusable Gradle modules

Shared DTOs, interfaces, and validation rules

✅ Used Feign/RestTemplate/WebClient for inter-service calls

✅ Handled distributed data

Via shared DB schemas (in early phase)


✅ 3. GraalVM Native Image
🛠️ What I did:
I built and optimized a native image using GraalVM, reducing startup time and memory usage.

📘 In simple words:
Normal Java apps run on the JVM and load classes at runtime

GraalVM native images compile everything ahead-of-time into a small standalone binary

Super-fast startup

Low memory usage

Great for microservices or CLI tools

⚠️ Challenges I handled:
✅ Reflections don’t work automatically

Had to configure reflection metadata (reflect-config.json or annotations)

✅ Third-party libraries (e.g., Jackson, Hibernate, SLF4J) often need native hints

✅ Spring Boot support added via spring-native or now built-in in Spring Boot 3.x

✅ Used native-maven-plugin to automate build

✅ 4. Implementation of Automated-QM Service
🛠️ What I did:
I designed and implemented the Automated Quality Management (Auto-QM) service
that evaluates calls automatically based on flexible rule conditions.

⚙️ Key functionality:
✅ Fetch calls periodically (e.g., every hour) from conversation-service

✅ Evaluate calls using rule sets:

Rule types: duration checks, label presence, speech tag scores, AI-based questions

✅ Apply subrules and scoring logic

Rule composition: ranges, multi-equals, yes/no scores

✅ Store results into DB (automated_reviews, attribute_sub_score, etc.)

✅ Track rule executions, retries, and errors (via executions, retry tables)

✅ Connected to AI system for subjective evaluation (e.g., "Did agent greet?")

✅ Used Spring + Kotlin with RabbitMQ and scheduler integration