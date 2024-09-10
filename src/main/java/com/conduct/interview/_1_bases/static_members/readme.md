`static` keyword in java could be applied to:
- variables (fields);
- methods;
- static blocks;
- inner classes;

Characteristics of static members:
- allocate memory just once, memory space is shared by all instances
- you don't need to create object in order to access the static member (associated with class, not instances of it)
- static members can't access none-static content
- static methods can be overloaded, not overridden
- static variable can be created just on the class level
- static blocks and variables are executed in the order they are present in the program

Static methods:
They can only directly call other static methods.
They can only directly access static data.
They cannot refer to this or super in any way.

Advantages:
Memory efficiency: 
Static members are allocated memory only once during the execution of the program, which can result in 
significant memory savings for large programs.
Improved performance: 
Because static members are associated with the class rather than with individual instances, they can be 
accessed more quickly and efficiently than non-static members.
Global accessibility: 
Static members can be accessed from anywhere in the program, regardless of whether an instance of the class 
has been created.
Encapsulation of utility methods: 
Static methods can be used to encapsulate utility functions that don’t require any state information from an object. 
This can improve code organization and make it easier to reuse utility functions across multiple classes.
Constants: 
Static final variables can be used to define constants that are shared across the entire program.
Class-level functionality: 
Static methods can be used to define class-level functionality that doesn’t require any state information from 
an object, such as factory methods or helper functions.

However, static variables have the disadvantage of being global, which means they can be accessed and changed by 
any part of the program, potentially leading to errors or security issues. They also make the code less flexible 
and modular, as they create a tight coupling between the class and the variable.