# Spring AOP (Aspect-Oriented Programming)

AOP pulls a cross-cutting concern (logging, security checks, retries,
transactions) out of business logic and into one reusable module,
applied to many places by configuration instead of by copy-pasting a
call at the start of every method.

## Vocabulary

- **Aspect**: the module of cross-cutting behavior itself (e.g. a
  `LoggingAspect` class).
- **Join point**: a point in execution the aspect can hook into - in
  Spring AOP, always a method call.
- **Pointcut**: an expression selecting which join points an aspect
  applies to, e.g. `execution(* com.conduct.interview._3_spring._4_aop.*.*(..))`.
- **Advice**: the code that runs at a matched join point, and *when*
  relative to it - `@Before`, `@After`, `@AfterReturning`,
  `@AfterThrowing`, `@Around` (wraps the call - the only kind that can
  change arguments/return value or skip the call entirely).
- **Weaving**: physically wiring the aspect into the target. Spring AOP
  always does this at **runtime**, by proxying (see below) - unlike full
  AspectJ, which can weave at compile-time or class-load-time by
  rewriting bytecode directly, no proxy involved.

## Under the hood: it's the Proxy design pattern

Spring AOP does not modify your class's bytecode. It puts a **proxy** in
front of the real bean and only the proxy is ever handed out by the
container - every call goes proxy first, real method second (or not at
all, for `@Around` advice that decides not to `proceed()`).

Which proxy technology depends on what the target implements:

- Target implements at least one interface -> **JDK dynamic proxy**
  (`java.lang.reflect.Proxy`), which implements the same interface(s)
  and forwards calls through an `InvocationHandler`. This is what you
  get for `GreetingServiceImpl` here.
- Target implements no interface -> **CGLIB proxy**: a runtime-generated
  **subclass** of the target class that overrides every method. This is
  what you get for `PlainCalculator` here (no interface at all). You can
  force CGLIB even when an interface exists with
  `<aop:aspectj-autoproxy proxy-target-class="true"/>`.

Either way, the proxy is created and swapped in during
`postProcessAfterInitialization` - the exact same `BeanPostProcessor`
hook from [_2_bean_lifecycle](../_2_bean_lifecycle), via
`AnnotationAwareAspectJAutoProxyCreator`, registered by
`<aop:aspectj-autoproxy/>`.

This is also *why* self-invocation doesn't trigger advice: if a method
on the real object calls another method on `this`, that call never goes
through the proxy - only calls that arrive from outside the object, via
the injected/looked-up proxy reference, get intercepted.

Run `AopDemo` and check `getClass()` on each bean - one prints a
`$Proxy..` JDK proxy class implementing `GreetingService`, the other a
`...$$SpringCGLIB$$...` subclass of `PlainCalculator`.
