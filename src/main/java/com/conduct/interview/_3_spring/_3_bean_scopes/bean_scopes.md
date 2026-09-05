# Bean Scopes

Scope answers: "when I `getBean()` (or inject) this, do I get the same
instance as last time, or a new one?"

- **singleton** (default): one instance per container, cached and reused
  for every injection point and every `getBean()` call.
- **prototype**: a brand-new instance every time it's requested.
- **request / session / application / websocket**: web-only scopes, one
  instance per HTTP request / session / `ServletContext` / WebSocket
  session - need an active web request to make sense of, so they aren't
  demoed here (see `_10_spring_mvc_and_dispatcher_servlet` for the web
  layer these rely on).

## The prototype-in-singleton problem

A singleton bean is built **once**. If it takes a prototype bean as a
plain constructor/setter dependency, that injection happens once too -
the "prototype" ends up frozen inside the singleton forever, defeating
the point of it being a prototype. `SingletonBeanNotFixed` demonstrates
exactly this: every call returns the same id.

Three ways to actually get a fresh prototype on every call:

1. **`ObjectFactory<T>` / `Provider<T>`** (`SingletonBeanWithObjectFactory`):
   inject a factory instead of the bean itself; call `.getObject()` (or
   `.get()`) each time you need a fresh instance. With annotations,
   Spring resolves an `ObjectFactory<T>` constructor parameter
   automatically; in plain XML the explicit idiom is
   `ObjectFactoryCreatingFactoryBean` pointed at the prototype bean's
   name (see `bean-scopes-context.xml`).
2. **`@Lookup` method injection** (`SingletonBeanWithLookup`): declare an
   abstract-ish method returning the prototype type; Spring overrides it
   at runtime (via a CGLIB subclass) to fetch a new instance from the
   container every time it's called.
3. **A scoped proxy** (`scoped-proxy="targetClass"` on the prototype
   bean's XML definition): the singleton holds a proxy that looks like
   the real type but forwards every method call through the container to
   fetch a fresh instance first - transparent to the singleton's code,
   which doesn't even need to know scoping is involved.

## Under the hood

Scope isn't a property of the bean *instance* - it's a strategy the
`BeanFactory` consults on every `getBean()` call, through the `Scope`
SPI (`Scope.get(name, objectFactory)`). For `"singleton"`, the default
implementation just checks a `ConcurrentHashMap` cache and creates+caches
on first miss. For `"prototype"`, there's no cache at all - `get()` calls
the `objectFactory` (which runs the constructor/wiring) fresh every time.
A scoped proxy is a normal CGLIB/JDK proxy (same mechanism as
[_4_aop](../_4_aop)) whose every method implementation is
`this.scope.get(beanName, objectFactory).theRealMethod(...)`.
