`@PostConstruct` is used in Spring to mark a method that is executed after
dependency injection. The method must not have arguments, as Spring automatically
invokes it after the bean is fully constructed and all dependencies are injected.
This ensures the method relies only on the injected fields for initialization.
It is ideal for tasks like resource initialization, validation, or starting
background jobs. The method is executed once in the bean's lifecycle, ensuring
a consistent and predictable setup phase.
