package knowyourproject.kotlin.methods.aggregation

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    val sum = numbers.reduce { acc, num -> acc + num }
    println(sum)
}