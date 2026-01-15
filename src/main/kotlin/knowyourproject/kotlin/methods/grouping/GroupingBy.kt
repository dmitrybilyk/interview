package knowyourproject.kotlin.methods.grouping

fun main() {
    val groupBy = listOf("a", "bb", "ccc")
        .groupBy { it.length }
    println(groupBy)
//        .eachCount()
    val counts = listOf("a", "bb", "ccc")
        .groupingBy { it.length }
//        .eachCount()
    println(counts)
}