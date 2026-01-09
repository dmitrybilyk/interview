package knowyourproject.kotlin.scopeFunctions

fun main() {
    val user = UserDetails("Dmytro")
    val user2: UserDetails = UserDetails("John")
    // 'Let' is good for null checks, transformation
    val name: String = user2.let {
        println(it)
        println(it)
        it.name
    }
    println(name)

    val name2 = user.run { name  + name
    "ddd"}
    println(name2)

    // 'also' returns the original object, useful for side effects
    println(user.also {
        it.name = "Dmytro"
        println(it.name)})
}

data class User(var name: String)