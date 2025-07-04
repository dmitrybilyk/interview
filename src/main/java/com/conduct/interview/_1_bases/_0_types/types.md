✅ 1. Primitive Types

These are the most basic types of data in Java. They are not objects and are stored directly in memory.
Type	Size	Example	Description
byte	8-bit	-128 to 127	Small integers
short	16-bit	-32,768 to 32,767	Medium integers
int	32-bit	-2^31 to 2^31-1	Standard integers
long	64-bit	Very large	Large integers
float	32-bit	3.14f	Floating point (less precise)
double	64-bit	3.14159	Floating point (more precise)
char	16-bit	'A', 'Ж'	Single Unicode character
boolean	1-bit (logical)	true, false	Logical values


✅ 2. Reference Types

These types refer to objects in memory (unlike primitives).
Type	Example	Description
String	"Hello"	Sequence of characters
Arrays	int[] nums = {1, 2, 3}	Collection of elements
Classes	MyClass obj = new MyClass()	Custom-defined objects
Interfaces	Runnable, List, etc.	Abstract behavior


✅ 3. Wrapper Classes

Java provides object wrappers for each primitive type so they can be used as objects (e.g. in collections).
Primitive	Wrapper
int	Integer
double	Double
boolean	Boolean
char	Character

These are used automatically in many cases (called autoboxing/unboxing).


✅ 4. Generic Types (Generics)

Generics let you define classes or methods with types as parameters.

Examples:

List<String> names = List.of("Anna", "John");
Map<String, Integer> ages = Map.of("John", 30);

This allows type safety when using collections.
🔍 Code Example:

int age = 25;                        // primitive
String name = "Alice";              // reference type
boolean isActive = true;            // primitive
Integer wrappedAge = age;           // wrapper class
int[] scores = {95, 80, 70};        // array
List<String> names = List.of("A", "B"); // generic list
