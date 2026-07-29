# SOLID Principles

**S - SRP:**  
One reason to change.  
Increases testability and cohesion.  
*Example:* `Book` and `PrintSomething` (printing)  
should be separate.

**O - OCP:**  
Open for extension, closed for modification.  

**L - LSP:**  
It should be possible to use subtypes whenever base type 
could be used.

Ostrich is a bird but it can't fly.

Whenever base Car has some speed restrictions it's child also
must have those restrictions.

Method in subtype should not declare more exception than
method in parent.

Subtypes should maintain the constraints of the base class.

Overriding method should not widen the method visibility to public.

Parameters in overriding method should not change (hierarchically).

Return type should not change (hierarchically).

Pre-condition (some checks before actual code in method) should not 
change.

Post-condition (at the end of method some reduce must happen).

**I - ISP:**  
Use small, specific interfaces instead of one broad  
interface.  
*Example:* Split a broad interface into smaller ones  
to avoid unused methods in unrelated classes.

**D - DIP:**  
Depend on abstractions, not concretions.  
*Example:* `Book` depends on `Printer` interface,  
not directly on `ConsolePrinter`.
