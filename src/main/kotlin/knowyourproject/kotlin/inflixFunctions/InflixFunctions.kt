package knowyourproject.kotlin.inflixFunctions

fun main() {
    println("Hello, infix functions!")
    val person = Person("John Doe", 30)
    person pay 1000
}

infix fun Person.pay(amount: Int) {
    println("Paying for services - $amount")
}

data class Person(val name: String, val age: Int)