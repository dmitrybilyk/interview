package knowyourproject.kotlin.inlineFunctions

fun main() {
    println("=== Without inline ===")
    repeatAction(3) {
        println("Doing something...")
    }

    println("\n=== With inline ===")
    repeatActionInline(3) {
        println("Doing something faster...")
    }
}

/**
 * Normal function using a lambda parameter.
 */
fun repeatAction(times: Int, action: () -> Unit) {
    for (i in 1..times) {
        action()
    }
}

/**
 * Inline version of the same function.
 */
inline fun repeatActionInline(times: Int, action: () -> Unit) {
    for (i in 1..times) {
        action()
    }
}
