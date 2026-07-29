# Immutable Class in Java

In Java, an **immutable class** is one whose instances cannot be modified
after they are created. This is especially useful in **concurrent programming**
or when dealing with shared resources. Immutable objects help avoid unintended
side effects.

## Key Characteristics of an Immutable Class

1. **All fields are `final`**: Once assigned, their values cannot be changed.
2. **No setter methods**: There are no methods to modify the fields after
   construction.
3. **Fields are private**: Direct access to the fields is restricted.
4. **The class is `final`**: This prevents subclassing, which could introduce
   mutability.
5. **Defensive copies for mutable fields**: If a field holds mutable data
   (e.g., arrays, lists), return copies rather than the original reference.

## Example of an Immutable Class

```java
public final class ImmutablePerson {
    private final String name;
    private final int age;
    
    // Constructor
    public ImmutablePerson(String name, int age) {
        this.name = name;
        this.age = age;
    }
    
    // Getters (No setters)
    public String getName() {
        return name;
    }
    
    public int getAge() {
        return age;
    }

    // Defensive copying for a mutable field
    private final List<String> attributes;

    public ImmutablePerson(String name, int age, List<String> attributes) {
        this.name = name;
        this.age = age;
        this.attributes = new ArrayList<>(attributes); // Copy for safety
    }

    public List<String> getAttributes() {
        return new ArrayList<>(attributes); // Return a copy
    }
}
