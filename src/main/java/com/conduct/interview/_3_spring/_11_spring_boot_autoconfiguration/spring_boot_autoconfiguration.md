# Spring Boot Autoconfiguration

`@SpringBootApplication` is three annotations in a trenchcoat:
`@Configuration` + `@ComponentScan` + `@EnableAutoConfiguration`.

The interesting one is `@EnableAutoConfiguration`. At startup it loads a
list of candidate `@Configuration` classes from
`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`
(one per line, shipped inside `spring-boot-autoconfigure.jar` - before
Boot 2.7 this list lived in `META-INF/spring.factories` instead) and
tries to apply every single one of them.

"Tries" is the key word - almost every autoconfiguration class is
wrapped in `@Conditional...` annotations so it only actually activates
when it makes sense:

- **`@ConditionalOnClass(SomeThirdPartyClass.class)`**: only if that
  class is present on the classpath (e.g. Hibernate's autoconfiguration
  only runs if `org.hibernate.SessionFactory` can be loaded).
- **`@ConditionalOnMissingBean`**: only if *you* haven't already defined
  your own bean of that type - this is why your own `@Bean` always wins
  over Boot's default.
- **`@ConditionalOnProperty`**: only if a given `application.properties`
  key is set (or absent, or equals a specific value).

## Under the hood, with the magic switched off

`@Conditional` isn't specific to Boot - it's core Spring
(`org.springframework.context.annotation.Conditional`), and at its heart
it's nothing more than "check something with plain Java, then decide
whether to register a bean definition." `ManualAutoConfigurationDemo`
reimplements the `@ConditionalOnClass` idea by hand, with no
annotations at all:

```java
if (ClassUtils.isPresent("com.some.library.Client", classLoader)) {
    context.registerBean("someLibraryClient", SomeLibraryClient.class);
}
```

That single `if` - repeated for every third-party library Boot knows
about, reading its result from a pre-computed list instead of scanning
the classpath fresh each time for performance - **is** what
`@ConditionalOnClass` compiles down to at runtime
(`OnClassCondition.matches()` calls exactly this kind of classpath
check). Run the demo: it registers a `JacksonAvailableBean` because
Jackson actually is on this project's classpath, and skips a
`SomeMissingLibraryBean` because it isn't - printing which decision it
made and why, the same way Boot's own
`--debug`/`ConditionEvaluationReport` does at real startup.
