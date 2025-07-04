Kotlin повністю працює на JVM і має повну сумісність із Java. Ми можемо використовувати Java SDK, 
сторонні Java-бібліотеки, і навіть писати Java-код поруч із Kotlin-кодом в одному проєкті. При цьому Kotlin додає 
сучасні можливості, такі як null-безпека, data-класи, лямбди, функції-розширення та інше — все це підвищує безпечність 
і читабельність коду.

🟦 Основні переваги та фічі Kotlin

// ✅ 1. Нульова безпека (null safety)
// Kotlin не дозволяє null без явного зазначення `?`
val name: String = "Dmytro"
// val n: String = null // ❌ помилка

val nullableName: String? = null
val length = nullableName?.length ?: 0 // ?. — безпечний виклик, ?: — значення за замовчуванням
println("Довжина імені: $length") // 0



// ✅ 2. Лямбда-функції
val sum = { a: Int, b: Int -> a + b }
println("Сума: ${sum(3, 4)}") // 7

// Застосування у колекціях
val nums = listOf(1, 2, 3, 4)
val doubled = nums.map { it * 2 }
println(doubled) // [2, 4, 6, 8]



// ✅ 3. Extension-функції — додаємо методи до існуючих типів
fun String.capitalizeWords(): String =
split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

val text = "kotlin is awesome"
println(text.capitalizeWords()) // Kotlin Is Awesome

// ✅ 6. Smart casting — автоматичне приведення типу після перевірки
fun printLength(value: Any) {
if (value is String) {
println("Довжина: ${value.length}") // value автоматично стає String
}
}
printLength("Kotlin") // Довжина: 6

kotlin
Copy
Edit
val doubled = list.map { it * 2 }
✅ 3. Функції-розширення (extension functions)
Додають нову поведінку до існуючих класів

kotlin
Copy
Edit
fun String.capitalizeWords() = split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }
✅ 4. data-класи
Автоматично генеруються toString(), equals(), hashCode(), copy()

kotlin
Copy
Edit
data class User(val name: String, val age: Int)
✅ 5. Sealed-класи
Гарантують, що всі підкласи відомі під час компіляції

kotlin
Copy
Edit
sealed class Result  
data class Success(val data: String): Result()  
data class Error(val msg: String): Result()
✅ 6. Smart casting
Після перевірки типу або null — автоматичне приведення типу

kotlin
Copy
Edit
if (x is String) println(x.length)
✅ 7. inline-функції
Вставляють тіло функції та лямбди напряму у виклик, економлять ресурси

kotlin
Copy
Edit
inline fun log(msg: String, block: () -> Unit) { println(msg); block() }
✅ 8. Делегація (by)
Делегування реалізації інтерфейсу іншому об’єкту

kotlin
Copy
Edit
class Service(logger: Logger) : Logger by logger
✅ 9. Колекції та функціональні API
map, filter, fold, groupBy — працюють з імутабельними колекціями

kotlin
Copy
Edit
val result = list.filter { it > 5 }.map { it * 2 }
✅ 10. when-вираз
Покращена альтернатива switch, підтримує всі типи

kotlin
Copy
Edit
when (x) {
is Int -> println("Int")
in 1..10 -> println("In range")
else -> println("Unknown")
}



# Kotlin

Kotlin is a modern, statically typed language developed by JetBrains. It
runs on the JVM, can be compiled to JavaScript or native code, and is
fully interoperable with Java.

## Key Features

1. **Java Interoperability**  
   Fully interoperable with Java, allowing seamless integration with
   existing Java libraries and frameworks.

2. **Concise Syntax**  
   Reduces boilerplate code with features like type inference, default
   parameters, and data classes.

3. **Null Safety**  
   Prevents `NullPointerException` with built-in null safety using `?` for
   nullable types.

4. **Functional Programming**  
   Supports lambdas, higher-order functions, and immutability.

5. **Extension Functions**  
   Extends existing classes with new functionality without modifying their
   source code.

6. **Coroutines**  
   Provides native support for asynchronous programming with lightweight
   coroutines.

7. **Data Classes**  
   Automatically generates useful methods (`toString()`, `equals()`,
   `hashCode()`, `copy()`) for classes designed to hold data.

8. **Multiplatform Support**  
   Allows code sharing across platforms (JVM, Android, JavaScript, Native).

9. **Android Development**  
   Preferred language for modern Android app development.

## Common Use Cases

- **Android App Development**: Preferred language for building Android apps.
- **Web Development**: Compiles to JavaScript for web applications.
- **Server-side Development**: Used with frameworks like Ktor and Spring.
- **Cross-Platform Development**: Kotlin Multiplatform for shared code.


Notes:
- no semicolon is required at the end of rows
- main method without args required
- **var** and **val**
- not required to set type of reference variable
- templates with $ instead of String concatenation
- smart cast (after checking type)
- default arguments and named parameters
- null safety (for nullable language enforces null checks)
- 