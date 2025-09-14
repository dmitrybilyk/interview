package kotlin_playground

fun main() {
    val processor = Processor("Parameter name")
    println(processor.getResult())

    val lam = { String }
    run { "fff" }

    listOf(1, 2, 3).map { it * 2 }

    println(funWithLambda(20) { it + 100 })

    val str = buildString {
        append("one")
        append("one")
    }
    println(str)

    "ddd".apply {
        this + this
    }

    val validator50 = validatorCreator(50)
    val validator150 = validatorCreator(50)

    println(validator150(20))
}

fun validatorCreator(value: Int): (Int) -> Boolean {
    return {input -> input >= value}
}

fun funWithLambda(a: Int, transform: (Int) -> Int): Int {
    return transform(a)
}

class Processor(val parameter: String) {
    fun process() {
        println("processing $parameter")
    }
}

fun Processor.getResult(): String {
    return "Result $parameter"
}