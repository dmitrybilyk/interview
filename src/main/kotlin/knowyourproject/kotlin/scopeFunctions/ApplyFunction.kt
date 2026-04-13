package knowyourproject.kotlin.scopeFunctions

fun main() {
    println(UserDetails("Dmy", 4).apply {
        name = "DmyDmy"
        age = 44
    })
    println(UserDetails("Dmy", 4).thenApply {
        name = "DmyDmy"
        age = 44
    })
}

fun <T> T.thenApply(block: T.() -> Unit): T {
    block()
    return this
}