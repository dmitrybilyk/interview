## Java Exceptions

In Java, exceptions are events that disrupt the normal flow of program
execution. They represent errors or unexpected conditions that occur during
runtime. Exceptions are categorized into two main types:

- **Checked Exceptions**: These are checked at compile time and must be either
  caught or declared in the method signature. Examples include
  `IOException` and `SQLException`.

- **Unchecked Exceptions**: These occur at runtime and include programming
  errors, such as `NullPointerException` and
  `ArrayIndexOutOfBoundsException`. They do not need to be declared or caught.

Java provides a robust exception handling mechanism using `try`, `catch`, and
`finally` blocks, allowing developers to manage errors gracefully and maintain
program stability.


# Exception Handling Best Practices

- **Do not swallow exceptions**: Always handle or log exceptions, never ignore them.

- **Catch specific exceptions**: Catch only the specific sub-classes of exceptions,
  not the general `Exception` or `Throwable` classes.

- **Wrap exceptions correctly**: Wrap exceptions in custom exceptions to preserve
  the stack trace.

- **Log or throw, never both**: Either log the exception or rethrow it, but don't do
  both in the same place.

- **Avoid throwing exceptions in `finally`**: Do not throw any exceptions from the
  `finally` block, as it may mask the original exception.

- **Handle only what you can**: Catch and handle only those exceptions that you can
  actually deal with.

- **Throw early, catch late**: Raise exceptions as soon as an error condition is
  detected and catch them at a higher, appropriate level in the code.

- **Terminate interrupted threads**: Ensure that threads are terminated when they are
  interrupted.

- **Document exceptions**: Always document the exceptions that your methods throw
  using Javadoc.
