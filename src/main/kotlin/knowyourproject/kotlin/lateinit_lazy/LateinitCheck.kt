package knowyourproject.kotlin.lateinit_lazy

//lateinit avoids ?

//You must control lifecycle correctly

//Compiler trusts you, runtime checks you
class App {
    lateinit var config: Config

    fun init() {
        println("Initializing config")
        config = Config("production")
    }

    fun run() {
        println("Running app in ${config.env}")
    }
}

class Config(val env: String)

fun main() {
    val app = App()

    // app.run() // ❌ uncomment to see runtime crash

    app.init()
    app.run()
}
