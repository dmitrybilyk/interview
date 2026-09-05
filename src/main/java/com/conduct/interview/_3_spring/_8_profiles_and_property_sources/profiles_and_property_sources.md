# Profiles and Property Sources

**`Environment`** is Spring's abstraction over "where configuration comes
from" - it merges many `PropertySource`s (system properties, environment
variables, `.properties`/`.yaml` files, command-line args) into one
`getProperty(key)` lookup, with a defined precedence order (in Spring
Boot: command-line args > env vars > `application.properties` >
defaults, roughly highest to lowest).

**Profiles** let a bean definition (or a whole `<beans>` block in XML, or
a `@Configuration` class/`@Bean` method) declare "only register me when
profile X is active" - the classic use is swapping a `dev` datasource for
a `prod` one without an `if` anywhere in your own code.

## Run it

`profiles-context.xml` declares two `<beans profile="...">` blocks, each
defining a `DataSourceConfig` bean with different values. Note the demo
uses `GenericXmlApplicationContext`, not `ClassPathXmlApplicationContext`
- the latter calls `refresh()` inside its constructor, before you get a
chance to set the active profile. `GenericXmlApplicationContext` lets you
`load(...)`, set the environment's active profiles, and only then call
`refresh()` yourself.

```java
GenericXmlApplicationContext context = new GenericXmlApplicationContext();
// profile must be set BEFORE load() - <beans profile="..."> is evaluated
// during XML parsing itself, not deferred until refresh()
context.getEnvironment().setActiveProfiles("dev");
context.load("classpath:.../profiles-context.xml");
context.refresh();
```

Run it once as-is (profile `dev`), then flip `ACTIVE_PROFILE` in
`ProfilesDemo` to `prod` and re-run to see the other bean definition
take effect - same XML file, different active profile.
