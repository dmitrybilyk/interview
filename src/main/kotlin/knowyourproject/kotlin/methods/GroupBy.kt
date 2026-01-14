package knowyourproject.kotlin.methods

data class Person(val name: String, val age: Int)

val people = listOf(
    Person("Alice", 21),
    Person("Bob", 25),
    Person("Charlie", 21)
)

fun main() {
    val grouped = people.groupBy { it.age }
    println(grouped)
}
