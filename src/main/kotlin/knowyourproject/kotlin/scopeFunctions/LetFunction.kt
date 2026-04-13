package knowyourproject.kotlin.scopeFunctions

fun main() {
    val userDetails = UserDetails("Dmytro", 44)
    println(userDetails?.let {
        it.name = "Dmytro2"
        it.age = 30
        it
    })

    println("res" + userDetails.then {
        it.name = "Dmytro3"
        it.age = 35
        it
    })
}

fun <T, R> T.then(block: (T) -> R): R {
    return block(this)
}