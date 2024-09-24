The Command pattern allows wrapping methods and parameters in objects to execute
them later. It also supports implementing undo operations and is an example of the
producer-consumer pattern.

Components:

Client: Configures commands.
Invoker: Executes commands.
Command: Defines an operation.
Receiver: Performs the work.
This decouples the object invoking operations from the one performing them.