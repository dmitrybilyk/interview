package knowyourproject.kotlin.extensionFunctions

fun main() {
    println("ext".doubleValue())
    val list = listOf(Student("Student name"), Student("St"))
    list.conditionalPrint()
}

fun String.doubleValue(): String {
    return this + this
}

fun List<Student>.conditionalPrint() {
    this.filter { student -> student.name.contains("name") }.forEach { println(it) }
}

data class Student(val name: String)