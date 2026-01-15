package knowyourproject.kotlin.methods

fun main() {
    val numbers = listOf("1", null, "3", "4", "5")
    val result = numbers.mapNotNull{ "  " + it}
    println(result)
}