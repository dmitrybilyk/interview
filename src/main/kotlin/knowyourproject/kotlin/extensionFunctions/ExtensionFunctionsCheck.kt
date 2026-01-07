package knowyourproject.kotlin.extensionFunctions

fun main() {
    val list = listOf(Student("Student name"), Student("St mm"))

//    list.nameFilterReturnsList("mm").forEach { println(it.name) }
    val df = list.differedNameFilterReturnsList()
    df("mm").forEach { println(it.name) }
//    val mmFilter = list.nameFilter("mm")
//    mmFilter().forEach { println(it.name) }

}

fun List<Student>.printSecondWord() {
    this.forEach { println(it.name.split(" ")[1]) }
}

fun List<Student>.nameFilterReturnsList(param: String): List<Student> {
    return this.filter { it.name.contains(param) }
}

fun List<Student>.differedNameFilterReturnsList(): (s: String) -> List<Student> {
    return { param -> this.filter { it.name.contains(param) } }
}

fun List<Student>.nameFilter(param: String): () -> List<Student> {
    return { this.filter { it.name.contains(param) } }
}