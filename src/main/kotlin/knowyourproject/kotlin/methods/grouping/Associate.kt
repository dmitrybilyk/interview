package knowyourproject.kotlin.methods.grouping

data class User(val name: String, val age: Int)

val users = listOf(
    User("Alice", 21),
    User("Bob", 25),
    User("Charlie", 21)
)

fun main() {
    val associated = users.associate { it.age to it.name }
    println(associated)
}