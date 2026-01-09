package knowyourproject.kotlin.scopeFunctions

fun main() {
    val userDetails = UserDetails("Dmytro", 44)
    println(userDetails?.run {
        name = "Dmytro2"
        age = 30
        this
    })
    println(userDetails?.thenDo {
        name = "Dmytro2"
        age = 30
        this
    })
}

fun <T, R> T.thenDo( block: T.() -> R): R {
    return block()
}