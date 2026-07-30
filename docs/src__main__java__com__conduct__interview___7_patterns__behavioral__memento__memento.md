# Memento Design Pattern

The **Memento Pattern** is a behavioral design pattern that allows an
object's state to be saved and restored without exposing its internal
structure. It provides a way to capture and externalize the internal
state of an object so that it can be restored later. This pattern is
particularly useful when an object needs to revert to a previous state,
such as in undo or rollback operations.

## Key Participants

1. **Originator**:
    - The object whose state needs to be saved or restored.
    - It creates a `Memento` containing a snapshot of its current state.
    - It can also restore its state using a `Memento`.

2. **Memento**:
    - A simple object that stores the state of the `Originator`.
    - The state is private and cannot be accessed by external objects,
      ensuring encapsulation.
    - Typically contains the same fields as the `Originator`.

3. **Caretaker**:
    - Manages the saved `Memento` objects.
    - It keeps track of `Memento` instances but does not inspect or
      modify the stored states.
    - Responsible for deciding when to save and restore the
      `Originator`'s state.

## Structure

- The **Originator** generates a **Memento** that holds its internal
  state.
- The **Caretaker** stores the **Memento** without altering or
  accessing its contents.
- When needed, the **Caretaker** provides the **Memento** back to the
  **Originator**, which restores its state from the **Memento**.

## When to Use

- You want to provide the ability to restore an object to a previous
  state (e.g., undo/redo functionality).
- You want to maintain encapsulation and prevent other objects from
  accessing or modifying the internal state of the object directly.
- Saving and restoring the state of complex objects where you need
  control over when the object state is saved or restored.

## Example

A typical example of the **Memento Pattern** is a text editor with undo
functionality. The editor saves the state of the document (text, cursor
position, etc.) as a `Memento` object. When the user presses undo, the
`Caretaker` provides the saved `Memento`, and the editor restores the
previous state from it.

## Advantages

- **Encapsulation**: The internal state of the `Originator` is protected
  and cannot be accessed directly by other objects.
- **Undo/Redo functionality**: Easily implement undo/redo features by
  storing and restoring `Memento` objects.
- **Simplifies state management**: It allows an object to maintain its
  previous states without being responsible for managing them directly.

## Disadvantages

- **Memory overhead**: If the state of the `Originator` is large or
  complex, saving multiple `Memento` objects can consume a lot of
  memory.
- **Serialization complexity**: Managing state and serialization of
  large objects can introduce complexity in the implementation.
