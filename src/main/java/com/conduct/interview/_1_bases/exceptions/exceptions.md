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
