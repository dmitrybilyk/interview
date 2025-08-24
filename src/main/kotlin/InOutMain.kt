@file:Suppress("UNCHECKED_CAST")

open class Student
class Graduate : Student()
class Human

fun main() {
    // Example 1: Contravariant (in T)
    val students: MutableList<Student> = mutableListOf()
    addStudent(students) // ✅ works, we can add Student and Graduate

    // Example 2: Contravariant with unknown generic
    val graduates: MutableList<Graduate> = mutableListOf()
    addStudentToUnknown(graduates) // ✅ only Graduate or subtypes of Graduate

    // Example 3: Covariant (out T)
    val someStudents: List<Student> = listOf(Graduate())
    readStudents(someStudents) // ✅ can read
    // someStudents.add(Student()) // ❌ cannot add
}

// 'in T' allows adding T or subtypes
fun <T: Student> addStudent(list: MutableList<in T>) {
    list.add(Student() as T) // ✅ if we cast safely
    list.add(Graduate() as T) // ✅ safe because Graduate : T
}

// if T is unknown, you cannot add a plain Student
fun <T: Student> addStudentToUnknown(list: MutableList<in T>) {
    // list.add(Student()) // ❌ compiler error
    list.add(Graduate() as T) // ✅ safe
}

// 'out T' allows reading only
fun <T: Student> readStudents(list: List<out T>) {
    val first: T = list[0] // ✅ can read
    println(first)
    // list.add(Student()) // ❌ cannot add
}
