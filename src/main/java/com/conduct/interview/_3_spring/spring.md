# Spring

Index of this topic. Every numbered folder has a short `*.md` (theory +
"under the hood" mechanism) and a runnable demo wired up via plain XML
(`ClassPathXmlApplicationContext`/`GenericXmlApplicationContext`) - no
embedded web server needed to run any of them, except where noted.

`POJO` - plain object, no framework dependency. `Java Bean` - a POJO
following a convention (no-arg constructor, private fields, public
getters/setters). `Spring Bean` - any object whose lifecycle is managed
by the Spring IoC container - none of the three are the same thing.

1. [_1_ioc_and_dependency_injection](_1_ioc_and_dependency_injection) - the container, DI styles, XML vs annotations vs Java config
2. [_2_bean_lifecycle](_2_bean_lifecycle) - every phase a bean goes through, in order, runnable end to end
3. [_3_bean_scopes](_3_bean_scopes) - singleton vs prototype, and three ways to fix "prototype injected into a singleton"
4. [_4_aop](_4_aop) - the Proxy pattern: JDK dynamic proxy vs CGLIB, advice types, pointcuts
5. [_5_transactions](_5_transactions) - `@Transactional` as an AOP proxy, propagation, isolation, a real rollback demo
6. [_6_bean_post_processors_and_factory_post_processors](_6_bean_post_processors_and_factory_post_processors) - how `@Autowired`/placeholders/proxies actually get applied
7. [_7_events](_7_events) - the Observer pattern, built into the container
8. [_8_profiles_and_property_sources](_8_profiles_and_property_sources) - `Environment`, `@Profile`-equivalent XML blocks
9. [_9_validation](_9_validation) - JSR-380 Bean Validation, plain and Spring-proxied
10. [_10_spring_mvc_and_dispatcher_servlet](_10_spring_mvc_and_dispatcher_servlet) - the Front Controller pattern (demo needs a servlet-test harness, see its md)
11. [_11_spring_boot_autoconfiguration](_11_spring_boot_autoconfiguration) - `@ConditionalOnClass` reimplemented by hand
12. [_12_spring_data_jpa](_12_spring_data_jpa) - repository interfaces are JDK dynamic proxies too
13. [_13_spring_security](_13_spring_security) - the filter chain (Chain of Responsibility, not a proxy) and the authentication mechanism
14. [_14_object_mapper](_14_object_mapper) - Jackson's `ObjectMapper`, independent of Spring itself
15. [_15_testing_spring_apps](_15_testing_spring_apps) - unit test vs a context slice vs `@SpringBootTest`

Start with 1-4 - almost everything after that is either "a specific
flavor of proxy" (5, 9, 12) or "a specific `BeanPostProcessor`" (6 and,
indirectly, most of the rest).
