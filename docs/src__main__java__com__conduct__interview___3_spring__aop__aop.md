### Spring AOP (Aspect-Oriented Programming)

Spring AOP allows for the separation of cross-cutting concerns, like logging
or security, from business logic, making code modular and maintainable.

#### Key Concepts
- **Aspect**: A modularized concern, e.g., logging, transaction handling.
  (the aspect of application we need to apply in many places)
- **Join Point**: A specific point in execution, like a method call.
  (WHERE to execute - in method call)
- **Advice**: Code to execute at a join point (e.g., `@Before`, `@After`).
  (WHAT to do and WHEN - "do something @Before method call")
- **Pointcut**: Expression to select join points for advice application.
  (expression to filter methods by packages and classes for instance)
- **Weaving**: Linking aspects with objects to create an advised object.
  (compile time, load, runtime weaving)

#### Example
```java
@Aspect
@Component
public class LoggingAspect {

[//]: # This is Advice Type - ***@Before*** - when to do ( before the method )
    ***execution(* com.example.service.*.*(..))*** <<< pointcut
    @Before("execution(* com.example.service.*.*(..))")

[//]: # This is a Join point - where to do ( in this method )
    public void logBefore() {

[//]: # This is Advice - what to do ( body of method )
        System.out.println("Executing method...");
    }
}
