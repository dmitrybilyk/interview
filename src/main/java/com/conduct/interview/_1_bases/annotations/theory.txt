Annotations are used to provide supplemental information about a program. They
start with `@` and do not change the action of the compiled program, but they
help associate metadata (information) with program elements like instance
variables, constructors, methods, and classes.

Unlike pure comments, annotations can influence how a program is treated by the
compiler.

For example, we can create custom annotations and use reflection to check if
they are present, running specific logic if needed, to generate some code
(like creation of builder class etc.)

Key concepts include:

- **`@Target`**: Specifies where the annotation can be applied (methods, fields,
  classes, etc.).
- **`@Retention`**: Determines whether the annotation is available at runtime
  or only at compile-time.
