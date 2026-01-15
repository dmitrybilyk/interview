package knowyourproject.kotlin.methods.grouping

data class UserPerson(val name: String, val age: Int)

val userPersons = listOf(
    UserPerson("Alice", 21),
    UserPerson("Bob", 25),
    UserPerson("Charlie", 21)
)

fun main() {
    val associated = userPersons.associateBy { it.age }
    println(associated)
}