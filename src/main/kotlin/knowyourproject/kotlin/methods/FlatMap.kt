package knowyourproject.kotlin.methods

fun main() {
    val words = listOf("hi", "bye")

    val letters = words.flatMap { it.toList() }

    println(letters)
}