# IoC and Dependency Injection

**Inversion of Control (IoC)**: instead of an object constructing (or looking
up) its own collaborators, something external hands them to it. Spring's
IoC container (`BeanFactory` / `ApplicationContext`) is that "something
external" - it reads bean definitions, builds the object graph, and wires
it together for you.

**Dependency Injection (DI)** is how the container hands the dependencies
over. Three styles, same idea:

- **Constructor injection** (preferred): dependency passed in a
  constructor - can be stored in a `final` field.
- **Setter injection**: dependency passed via a setter after construction.
- **Field injection** (`@Autowired` directly on a field): shortest to
  write, hardest to unit test (you can't construct the object without
  reflection or a container) - avoid outside of quick demos.

Why constructor injection wins in practice:
1. The field can be `final` - the object can never exist half-wired, and
   it's safe to share across threads.
2. A huge/many-argument constructor is an obvious code smell pointing at
   a class doing too much - setter/field injection hides that signal.
3. Testing is trivial: `new Car(new PetrolEngine())`, no container needed.

## Where bean definitions can live

The same container, three historical ways to describe the same beans:

1. **XML** (`<bean>` elements) - the original mechanism, fully external to
   the class. What this folder's demo uses.
2. **Annotations + component scanning** (`@Component`/`@Autowired` +
   `@ComponentScan`) - the class declares its own eligibility; the
   container discovers it by scanning the classpath.
3. **Java `@Configuration` classes** (`@Bean` methods) - type-safe,
   refactorable, still explicit like XML but written in Java.

Spring Boot then adds a fourth layer on top of all this -
[_11_spring_boot_autoconfiguration](../_11_spring_boot_autoconfiguration) -
which is really just a big pile of conditional `@Configuration` classes
Boot registers for you.

## Under the hood

An `ApplicationContext` doesn't hold real objects until it resolves
`BeanDefinition`s (metadata: class name, constructor-args, properties,
scope) into actual instances. For each singleton bean, at `refresh()`
time it roughly:

1. Finds/creates the `Constructor` via reflection and calls
   `Constructor.newInstance(args)` - this is "instantiation".
2. Uses reflection (`Field.set` / setter `Method.invoke`) to populate
   properties - this is "populating properties".
3. Runs `Aware` callbacks, `BeanPostProcessor`s, `@PostConstruct`,
   `InitializingBean.afterPropertiesSet()`, and any custom init method -
   see [_2_bean_lifecycle](../_2_bean_lifecycle) for the full sequence.

Run `IocAndDependencyInjectionDemo` and watch the console: every
singleton bean declared in `ioc-context.xml` is constructed **eagerly**,
during `refresh()` in the `ApplicationContext` constructor - before
`getBean()` is ever called. That eager pre-instantiation of singletons
(`DefaultListableBeanFactory.preInstantiateSingletons()`) is a deliberate
design choice: better to fail fast at startup than on the first request.
