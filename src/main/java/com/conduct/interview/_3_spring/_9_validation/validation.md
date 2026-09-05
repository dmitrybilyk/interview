# Bean Validation (JSR-380)

`@NotNull`, `@Size`, `@Min`, `@Email`, etc. come from **Jakarta Bean
Validation** (JSR-380) - a Java standard, not a Spring feature.
Hibernate Validator is the reference implementation and the one on this
project's classpath. You can use it with zero Spring involvement:

```java
Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
Set<ConstraintViolation<SignupRequest>> violations = validator.validate(request);
```

Spring integrates this in two places:

- **Web layer**: `@Valid`/`@Validated` on a `@RequestBody` controller
  parameter - an `ArgumentResolver` runs the validator before your
  method body executes, throwing `MethodArgumentNotValidException` on
  failure (see [_10_spring_mvc_and_dispatcher_servlet](../_10_spring_mvc_and_dispatcher_servlet)).
- **Any bean method**: `@Validated` on the class + `@Valid`/constraint
  annotations on method parameters - enabled by
  `MethodValidationPostProcessor`.

## Under the hood

`MethodValidationPostProcessor` is, once again, just a
`BeanPostProcessor` (see
[_6_bean_post_processors_and_factory_post_processors](../_6_bean_post_processors_and_factory_post_processors))
that wraps matching beans in an **AOP proxy** (see [_4_aop](../_4_aop))
whose advice runs the `Validator` against the arguments before letting
the real method run, throwing `ConstraintViolationException` if
anything fails - the real method body never executes on bad input.

## Run it

`ValidationDemo` does both: validates a `SignupRequest` object directly
with a plain `Validator` (no Spring), then calls a
`@Validated`-proxied `RegistrationService.register(...)` with a bad
argument and shows the proxy rejects it before `register`'s body ever runs.
