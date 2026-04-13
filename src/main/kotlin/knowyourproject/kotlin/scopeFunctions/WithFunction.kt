package knowyourproject.kotlin.scopeFunctions

fun main() {
    print(with(UserDetails()) {
        validate()
        save()
        "result"
    })
    print(withObject(UserDetails()) {
        validate()
        save()
        "result"
    })
}

fun <T, R> withObject(receiver: T, block: T.() -> R): R {
    return receiver.block()
}

fun UserDetails.validate() {
    println("Validating user details")
}

fun UserDetails.save() {
    println("Validating user details")
}