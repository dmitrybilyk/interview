fun main() {
    val mathBuilder = MathBuilder()
    mathBuilder.add(10)
    mathBuilder.add(20)
    mathBuilder.multiply(2)

    println(mathBuilder.result)
}

fun math(block: MathBuilder.() -> Unit): MathBuilder {
    val mathBuilder = MathBuilder()
    mathBuilder.block()
    return mathBuilder
}

class MathBuilder {
    var result = 0

    fun add(x: Int) {
        result += x
    }

    fun multiply(x: Int) {
        result *= x
    }
}

