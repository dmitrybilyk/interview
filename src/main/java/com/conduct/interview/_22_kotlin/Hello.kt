// Entry point
fun main() {
    println("=== Kotlin Language Features ===")
//    VariablesDemo().run()
//    FunctionsDemo().run()
//    ClassDemo().run()
//    ObjectDemo().run()
//    CollectionDemo().run()
////    CoroutinesDemo().run()
//    MiscDemo().run()
    DmytroDemo().test()
}

class ForNull {
    var value: String = "ddd"
    var forNull2: ForNull1 = ForNull1()
}

class ForNull1 {
    var value: String = "ddd"
}

fun String.printWithStars() {
    println("*** $this ***")
}

//"hello".printWithStars() // prints: *** hello ***


data class Data2Class(val name: String) {
    val name2: String? = null
    fun someFunc(any: Any) {
        if (any is Int) {
            println(any + 2)
        }
    }
}

class DmytroDemo {
    fun test() {


        var list = listOf("Abd", "Bb", "Cc", "Dd")
        var grouped = list.groupBy { it.last() }
        println(grouped)

//        list.gr

        var reduced = list.reduce { a, b -> a + b }
        println(reduced)

        val forNull: ForNull = ForNull()
        println(forNull.forNull2.value)

        var func = { a: Int, b: Int -> a * b}
        println(func(2, 3))
        val s = "ddd"
        s.printWithStars()

        val col = listOf(4, 5, 6)

        val doubled = col.map { it * 2 }
//
//        var city: String
//        city = "Kharkiv"
//        println(city)
//        var some = "Dmytro"
//        some = "Dmytro2"
//        val constant = null
////        constant = "Dmytro Constant 2"
//        println(some)
//        val dataClass = constant?.let { DataClass(it) }
//        println(dataClass?.data?.length)
//
//        println("my name is $some")
    }


    fun oneLineFunction(s: String = "Done"): Int = s.length
}

data class DataClass(val data: String)

/** Variables and Basic Types **/
class VariablesDemo {
    fun run() {
        // Immutable variable (like final)
        val name: String = "Dmytro"

        // Mutable variable
        var age: Int = 30
        age += 1

        // Type inference
        val city = "Lviv"

        // Nullable types
        val nickname: String? = null

        // Elvis operator and safe call
        val length = nickname?.length ?: 0

        println("Hello, my name is $name, age $age, from $city. Nickname length: $length")

        fun test2(a: Int = 3): String {
            println("Hello $a")
            return ""
        }

        val test22 = {a: Int, b: String -> println(b + a)}

        val test33 = {}
    }
}

/** Functions **/
class FunctionsDemo {
    fun run() {
        println("Sum: ${sum(3, 5)}")
        println("Max: ${max(10, 20)}")
    }

    // Simple function
    fun sum(a: Int, b: Int): Int = a + b

    // Default and named arguments
    fun greet(name: String = "Guest") = println("Hello, $name!")

    // Lambda expression
    val multiply: (Int, Int) -> Int = { a, b -> a * b }

    // Extension function
    fun String.capitalizeWords() = split(" ").joinToString(" ") { it.replaceFirstChar(Char::uppercase) }

    // Higher-order function
    fun operate(x: Int, y: Int, op: (Int, Int) -> Int): Int = op(x, y)

    // Inline function
    inline fun withLog(message: String, block: () -> Unit) {
        println(">>> $message")
        block()
        println("<<< Done")
    }

    // Tail recursion
    tailrec fun factorial(n: Int, acc: Int = 1): Int =
        if (n <= 1) acc else factorial(n - 1, acc * n)

    // Local function
    fun outer() {
        fun inner() = println("Inner function")
        inner()
    }

    // Generic function
    fun <T> printList(list: List<T>) {
        list.forEach { println(it) }
    }

    // Destructuring
    fun destructure(pair: Pair<Int, String>) {
        val (id, name) = pair
        println("ID: $id, Name: $name")
    }

    private fun max(a: Int, b: Int) = if (a > b) a else b
}

/** Classes, Inheritance, Interfaces **/
class ClassDemo {
    fun run() {
        val user = User("John", 25)
        println(user.greet())

        val employee = Employee("Alice", 30, "Developer")
        println(employee.greet())

        val bird: Animal = Bird()
        bird.speak()
    }

    open class User(val name: String, var age: Int) {
        open fun greet() = "Hi, I'm $name and I'm $age years old"
    }

    class Employee(name: String, age: Int, val role: String) : User(name, age) {
        override fun greet() = super.greet() + ", working as $role"
    }

    interface Animal {
        fun speak()
    }

    class Bird : Animal {
        override fun speak() = println("Tweet!")
    }
}

/** Objects, Companion, Delegation **/
class ObjectDemo {
    fun run() {
        Singleton.printHello()
        val config = Config.getInstance()
        println("Port: ${config.port}")
        println("Service: ${Service().log()}")
    }

    // Singleton object
    object Singleton {
        fun printHello() = println("Hello from Singleton!")
    }

    // Object expression (anonymous class)
    val runnable = object : Runnable {
        override fun run() = println("Running...")
    }

    // Companion object
    class Config private constructor(val port: Int) {
        companion object {
            fun getInstance() = Config(8080)
        }
    }

    // Interface delegation
    interface Logger {
        fun log(): String
    }

    class ConsoleLogger : Logger {
        override fun log() = "Logging to console..."
    }

    class Service(logger: Logger = ConsoleLogger()) : Logger by logger
}

/** Collections and Lambdas **/
class CollectionDemo {
    fun run() {
        val list = listOf("a", "b", "c")
        list.map { it.uppercase() }.forEach { println(it) }

        val map = mapOf("k1" to 1, "k2" to 2)
        println(map["k1"])

        val mutableSet = mutableSetOf(1, 2, 2, 3)
        println(mutableSet)
    }
}

///** Coroutines (basic example) **/
//class CoroutinesDemo {
//    fun run() {
//        kotlinx.coroutines.runBlocking {
//            kotlinx.coroutines.launch {
//                kotlinx.coroutines.delay(100)
//                println("From coroutine!")
//            }
//            println("Main before delay...")
//            kotlinx.coroutines.delay(200)
//            println("Main after delay...")
//        }
//    }
//}

/** Miscellaneous Features **/
class MiscDemo {
    fun run() {
        // Sealed class
        val response: Response = Response.Success("OK")
        val message = when (response) {
            is Response.Success -> "Success: ${response.data}"
            is Response.Error -> "Error: ${response.message}"
            else -> {}
        }
        println(message)

        // Enum
        val status = Status.RUNNING
        println("Status ordinal: ${status.ordinal}")

//        // Type alias
//        val handler: ClickHandler = { println("Clicked!") }
//        handler()

        // Inline class (value class)
        val email = Email("a@b.com")
        println("Email.kt: $email")

        // Reflection
        val clazz = Email::class
        println("Class name: ${clazz.simpleName}")
    }

    sealed class Response {
        data class Success(val data: String) : Response()
        data class Error(val message: String) : Response()
    }

    enum class Status {
        STARTED, RUNNING, COMPLETED
    }

//    typealias ClickHandler = () -> Unit

    @JvmInline
    value class Email(val value: String)
}
