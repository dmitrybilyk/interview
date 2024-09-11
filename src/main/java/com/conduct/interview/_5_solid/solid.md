SOLID Principles

S - Single Responsibility Principle (SRP)
Every class should have just one reason to be changed. It’s easier to change, test, 
and read such classes. High cohesion is achieved when all related actions are grouped 
in one place. Loose coupling correlates with high cohesion, as tightly coupled classes 
lead to changes in many places when you change one thing.

Example: Class Book and PrintSomething (which handles printing) should be separate. This also allows PrintSomething to be reused.


O - Open/Closed Principle (OCP)
Classes should be open for extension but closed for modification. 
Adding new features should be done by adding new code, not editing existing code.

Example: Instead of modifying PrintSomething, extend it with FancyPrinter for additional 
formatting.


L - Liskov Substitution Principle (LSP)
Derived classes should be able to replace their parent classes without altering the correctness of the program.

Example: If FancyPrinter extends PrintSomething, you should be able to use FancyPrinter wherever PrintSomething is expected without breaking functionality.
I - Interface Segregation Principle (ISP)
Create smaller, focused interfaces with fewer responsibilities rather than a single broad interface. This avoids forcing classes to implement methods they don’t need.

Example: Split a broad interface into smaller, specific interfaces to prevent empty implementations in unrelated classes.
D - Dependency Inversion Principle (DIP)
Depend on abstractions, not on concrete implementations. Polymorphism and Dependency Injection (DI) help achieve Inversion of Control (IoC). With abstractions, testing, extending, and deployment become easier.

Example: Book should depend on an interface Printer instead of directly depending on ConsolePrinter.