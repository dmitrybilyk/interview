package knowyourproject.kotlin.dsl

@DslMarker
annotation class SchoolDsl

data class Student(
    val name: String,
    val age: Int
)

data class Group(
    val name: String,
    val students: List<Student>
)

@SchoolDsl
class StudentBuilder {
    var name: String = ""
    var age: Int = 0
    fun build() = Student(name, age)
}

@SchoolDsl
class GroupBuilder(private val name: String) {
    private val students = mutableListOf<Student>()

    fun student(name: String, age: Int) {
        students += Student(name, age)
    }

    fun student(block: StudentBuilder.() -> Unit) {
        val builder = StudentBuilder().apply(block)
        students += builder.build()
    }

    fun build() = Group(name, students)
}

fun group(name: String, block: GroupBuilder.() -> Unit): Group =
    GroupBuilder(name).apply(block).build()

fun main() {
    val group = group("Group A") {
        student("Alice", 20)
        student("Bob", 22)
        student {
            name = "Charlie"
            age = 19
        }
    }

    println(group)
}
