package knowyourproject.kotlin.lambda

class LambdaCheck {
}

fun main() {
    println("Hello, Lambda!")

    val l = { println("Lambda") }
    l()

    val equalsToS = doSomething("s")
    val equalsToB = doSomething("b")

    val c = "s"

    println(equalsToB(c))
//    equalsToB(c)
    val plusCalculator = buildCalculator("+")
    println(plusCalculator(1, 2))

    println(doAnotherThing { "ddd" })
}


fun doAnotherThing(block: () -> String): Int {
    println(block())
    return 7
}

fun doSomething(b: String): (param: String) -> Boolean {
    return { param -> if (b == param) true else false }
}

fun buildCalculator(operation: String): (Int, Int) -> Int {
    if (operation == "+") {
        return { a, b -> a + b }
    }
    return { a, b -> a - b }
}