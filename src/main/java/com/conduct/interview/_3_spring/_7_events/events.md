# Application Events

Spring's `ApplicationEvent`/`ApplicationListener` is the GoF Observer
pattern, built into the container: a publisher fires an event without
knowing or caring who (if anyone) is listening; the container fans it
out to every matching listener, decoupling the two sides completely.

Two ways to listen, same mechanism underneath:

- **Implement `ApplicationListener<MyEvent>`** - the classic, always-on
  interface style, works in any context, no annotation processing needed.
- **`@EventListener`** on any method of any bean - detected by
  `EventListenerMethodProcessor`, which needs
  `<context:annotation-config/>` (or component scanning) to be registered
  at all.

## Under the hood

`AbstractApplicationContext.publishEvent(event)` hands the event to a
`SimpleApplicationEventMulticaster`, which keeps a plain list of
registered listeners and calls each one whose generic type matches the
event, **synchronously, on the caller's thread, in registration order**,
by default. That means a slow or throwing listener blocks (or breaks)
the publisher - `@EventListener(..., condition = ...)` and
`@Async` (with a `TaskExecutor` configured on the multicaster) are the
two ways to change that.

## Run it

`OrderPlacedEvent` is published once. `AuditLogListener` (interface
style) and `emailListener` (an `@EventListener`-annotated method) both
receive it, proving both styles work off the same publish call.
