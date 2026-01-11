package knowyourproject.kotlin.lateinit_lazy

//Created once

//Created only when accessed

//No risk of “not initialized”
class App2 {
    val config by lazy {
        println("Creating config")
        Config2("production")
    }

    fun run() {
        println("App started")
        println("Environment: ${config.env}")
    }
}

class Config2(val env: String)

fun main() {
    val app = App2()

    println("Before run")
    app.run()
    app.run()
}
