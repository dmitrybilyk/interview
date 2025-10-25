package knowyourproject.kotlin.lambda

fun main() {

//    We can save lambda as a variable
    val l = { println("from lambda") }
    l()

//    We can pass lambda as a parameter into the function
    repeatAction(2, { println("Repeating") })

//    We can return lambda from the function
//    For instance we want behavior that will always multiply by 2, another by 3 etc.
    val multiplierBy2 = multiplier(2)
    val multiplierBy3 = multiplier(3)

    println(multiplierBy2(10))
    println(multiplierBy3(10))

//    Or we return when we would like to implement conditional logic inside
    val vipDiscountCalculator = getDiscountCalculator("VIP")
    val regularDiscountCalculator = getDiscountCalculator("REGULAR")

    println(vipDiscountCalculator(50.0))
    println(regularDiscountCalculator(50.0))

}

// We can pass lambda as a parameter
fun repeatAction(times: Int, action: () -> Unit) {
    repeat(times) {
        action() // call the behavior that was passed
    }
}

// We can return lambda from the function
fun multiplier(f: Int): (Int) -> Int {
    return { value -> value * f }
}

fun getDiscountCalculator(customerType: String): (Double) -> Double {
    return when (customerType) {
        "VIP" -> { price -> price * 0.8 }
        "REGULAR" -> { price -> price * 0.9 }
        else -> { price -> price }
    }
}
