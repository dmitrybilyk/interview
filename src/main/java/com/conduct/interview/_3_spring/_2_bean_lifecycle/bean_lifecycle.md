The Spring Bean lifecycle includes several phases that every bean goes through from
its creation to its disposal. By interacting with these phases, it is possible to
set up dependencies, manage resources, and integrate external libraries into the
Spring framework. The lifecycle consists of the following steps:

- **Instantiation**: Bean is created by executing the constructor.
- **Populating properties**: Setter methods are called to populate bean properties.
- **Aware hooks**: Interfaces such as `BeanNameAware`, `BeanFactoryAware`, and
  `ApplicationContextAware` are invoked to pass context-related information.
- **BeanPostProcessor**: Includes two methods:
    - `postProcessBeforeInit`: Called before initialization.
    - `postProcessAfterInit`: Called after initialization.
- **PostConstruct annotation**: Executed after `postProcessBeforeInit` and before
  `postProcessAfterInit`.
- **InitializingBean**: `afterPropertiesSet` method is executed for custom
  initialization logic.
- **CustomInitMethod**: A user-defined initialization method can be called after
  bean properties are set.
- **PreDestroy annotation**: Invoked before the bean is destroyed to perform cleanup.
- **DisposableBean**: `destroy` method is executed for custom destruction logic.
- **Custom destroy method**: A user-defined destroy method is called during
  bean destruction.

Each individual hook has its own tiny demo class under `phases/_1.../_12...`
(numbered in lifecycle order) if you want to see one step in isolation.

## Run the whole sequence at once

`BeanLifecycleDemo` wires `SomeBean`/`AnotherSomeBean`/`ManagedResource`
from `bean-lifecycle-context.xml`. Run it and you'll see steps 1-9 for
`someBean` (constructor through `afterPropertiesSet`), then
`context.close()` triggers steps 10-12.

One real gotcha this demo exposes: `SomeBean` declares itself a
`BeanPostProcessor` (step 6), but **it never sees its own step 6** - and
it doesn't see it for `anotherSomeBean` either (Spring even logs a WARN
about it: `anotherSomeBean ... is not eligible for getting processed by
all BeanPostProcessors`). Both are consequences of the same rule: a bean
only gets processed by post-processors that are **already fully
registered** at the time it's created.

- `anotherSomeBean` is pulled in as a dependency *while `someBean` is
  still being populated* (its setter needs it) - at that point `someBean`
  itself isn't finished, so it isn't in the post-processor list yet.
- `someBean` obviously can't be in the post-processor list before it
  finishes being created either - nothing can process itself.

`managedResource`, declared after `someBean` in the XML, is created only
once `someBean` has fully finished and been registered as an active
`BeanPostProcessor` - so it's the one bean in this file that actually
gets a step-6 `postProcessBeforeInitialization`/`postProcessAfterInitialization`
pair logged, with its own `@PostConstruct` running in between them.

Destroy callbacks (`@PreDestroy`, `DisposableBean`, a custom
`destroy-method`) only run when the context is explicitly closed (or a
JVM shutdown hook was registered via `context.registerShutdownHook()`).
Forget to call `close()` in a plain `main()` and step 10-12 simply never
happen - a common source of confusion.

## Practical use cases (not just print statements)

`ManagedResource` shows the shape these hooks take in real code: acquire
an expensive resource (`@PostConstruct`) once the bean is fully wired,
release it (`@PreDestroy`) before the container discards the bean. Swap
"connection" for a real connection pool, a Kafka consumer, a scheduled
executor, a temp file, a cache warm-up, etc. - anywhere a bean's
lifetime should bound a resource's lifetime, this is where that logic
belongs, rather than in the constructor (dependencies may not be set
yet) or a random method called manually later.

## Under the hood

All of this is orchestrated by `AbstractAutowireCapableBeanFactory`
around a single method, `initializeBean()`, called once per bean, right
after properties are populated:

```
invokeAwareMethods(bean)
wrapIfNecessary: postProcessBeforeInitialization on every registered BeanPostProcessor
invokeInitMethods: @PostConstruct -> afterPropertiesSet() -> custom init-method
wrapIfNecessary: postProcessAfterInitialization on every registered BeanPostProcessor
```

`@PostConstruct`/`@PreDestroy` are not magic - they are themselves
implemented as a `BeanPostProcessor`
(`CommonAnnotationBeanPostProcessor`), registered by
`<context:annotation-config/>` in XML (or automatically by
`AnnotationConfigApplicationContext`). It scans the bean's methods once
via reflection and caches which ones are annotated, so it doesn't have
to reflect on every bean creation. This is also why `@Autowired` and
AOP proxying (see [_4_aop](../_4_aop)) can hook into the exact same
`postProcessAfterInitialization` step to swap the real bean for a proxy
before anyone else gets a reference to it.
