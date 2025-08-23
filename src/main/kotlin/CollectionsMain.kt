//fun main() {
//
////    val predicate = { value: Int -> value > 3}
////    val intList = listOf(4, 5, 6, 2, 6, 3)
////
////    val (bigger, smaller) = intList.partition(predicate)
////
////    println(bigger)
////    println(smaller)
//
//    val library = listOf(
//        Book("Kotlin in Action", listOf("Isakova", "Elizarov", "Aigner", "Jemerov")),
//        Book("Atomic Kotlin", listOf("Eckel", "Isakova")),
//        Book("The Three-Body Problem", listOf("Liu"))
//    )
//
//    val authors = library.map { it.authors }
//    println(authors)
//    // [[Isakova, Elizarov, Aigner, Jemerov], [Eckel, Isakova], [Liu]]
//}
//
//class Book(val title: String, val authors: List<String>)