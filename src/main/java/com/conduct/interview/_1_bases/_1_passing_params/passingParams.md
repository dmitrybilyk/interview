# Passing Variables in Java: Primitives vs. Objects

In Java, variables are passed by value. However, the behavior differs
between primitives and objects.

## Primitives

When you pass a **primitive type** (e.g., `int`, `double`, `boolean`)
to a method, Java copies the value. Changes to the parameter do not
affect the original variable.

## Objects

When passing an **object**, Java copies the reference to the object,
not the object itself. Both the original reference and the method
parameter point to the same object.

### Modifying Fields

If you modify the object's **fields** inside the method, the changes
are visible outside the method.

### Changing References

If you reassign the object reference inside the method, it does not
affect the original object. The original reference remains pointing
to the same object.

## Summary

- **Primitives**: Passed by value; changes inside a method do not
  affect the original variable.
- **Objects**: Reference is passed by value; changes to object fields
  are visible outside the method, but reassigning the reference does
  not affect the original object.
