The Strategy pattern allows defining a comunity of algorithms, encapsulating
each one, and making them interchangeable. The algorithm can vary independently
of clients using it.

Components:
Strategy: Common interface for all strategies.
Concrete Strategies: Implement variations of the algorithm.
Context: Uses a strategy and can switch between them.
