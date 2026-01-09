package knowyourproject.kotlin.scopeFunctions

fun main() {
    print(UserDetails().also {
        it.name = "DmyDmy"
        it.age = 44
    })
    print(UserDetails().thenAlso {
        it.name = "DmyDmy2"
        it.age = 444
    })
}

fun <T> T.thenAlso(block: (T) -> Unit): T {
    block(this)
    return this
}