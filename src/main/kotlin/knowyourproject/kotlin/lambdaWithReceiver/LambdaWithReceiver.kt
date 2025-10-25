package knowyourproject.kotlin.lambdaWithReceiver

fun main() {
//    Regular lambda
    val greet = { name: String -> println("Hello, $name") }
    println(greet("Dmytro"))

//    (T) -> R	normal lambda — takes T as argument
//    T.() -> R	lambda with receiver — acts as if you’re inside a T

    val introduce: String.() -> Unit = {
        println("My name is $this") // here 'this' refers to the String
    }

    "Dmytro".introduce()

//    .apply function is good example of kotlins labda with receiver

}