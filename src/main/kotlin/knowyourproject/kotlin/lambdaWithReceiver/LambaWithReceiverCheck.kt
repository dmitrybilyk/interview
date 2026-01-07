package knowyourproject.kotlin.lambdaWithReceiver

data class Student(val name: String)

val lr: Student.() -> Unit = {
    println("My name is $this")
    println("Hello, $name")
}

fun main() {
    Student("Dmytro").lr()
}