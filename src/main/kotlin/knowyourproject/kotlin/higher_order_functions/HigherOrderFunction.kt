package knowyourproject.kotlin.higher_order_functions

//A higher-order function is just a function that:
//takes another function as a parameter, or
//returns a function

fun main() {
    println(process("Hello", { it.uppercase() }))

    val list = listOf("  hello", "world  ")
    println(transformAll(list) { it.uppercase() })
    println(transformAll(list) { it.trim() })

    println(edit("hello", { it + " ." }))
}

fun process(value: String, action: (String) -> String): String {
    val result = action(value)
    return result
}

fun transformAll(
    list: List<String>,
    transform: (String) -> String
): List<String> {
    val result = mutableListOf<String>()
    for (s in list) {
        result.add(transform(s))
    }
    return result
}

fun edit(value: String, action: (String) -> String): String {
    return action(value)
}

