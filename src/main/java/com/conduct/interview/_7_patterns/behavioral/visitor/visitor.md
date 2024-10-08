The **Visitor pattern** in Java allows adding operations to objects without
modifying their classes. It is particularly useful when you want to perform
different actions on a group of objects without altering their structure.
The pattern involves two key components:

1. **Visitor Interface**: Declares visit methods for each type of element.
2. **Concrete Visitor**: Implements the visit methods, defining specific
   operations for each element type.
3. **Element Interface**: Declares an `accept` method, which takes a Visitor.
4. **Concrete Element**: Implements the `accept` method and calls the appropriate
   visit method on the Visitor.

The Visitor pattern is ideal when the operations on an object hierarchy change
frequently but the structure remains the same. It promotes open/closed
principle by keeping the object classes closed for modification but open for
new behaviors via visitors.
