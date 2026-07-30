# State Pattern

The **State Pattern** enables an object to change its behavior dynamically based  
on its internal state. Each state is encapsulated in its own class, and the  
context object delegates behavior to the current state. This pattern eliminates  
complex conditional logic and allows for cleaner transitions between states by  
letting each state manage its own transitions.

### Key Components:
- **State Interface**: Defines behavior common to all states.
- **Concrete States**: Implement specific behaviors for each state.
- **Context**: Holds the current state and switches between states dynamically.
