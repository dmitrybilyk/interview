fun main() {
//    val lambda = { a: String -> a + a }
//
//    fun useLambda(a: String, stringLambda: (a: String) -> String): String {
//        return stringLambda.invoke(a)
//    }
//
//    println( useLambda("ddd") { it + it + it })
    
    val task = Task("task1") { println("task 1 is executing") }
    task.run()

}

class Task(val name: String, val action: () -> Unit) {
    fun run() = action()
}

class Scheduler {
    private val tasks = mutableListOf<Task>()

    fun task(name: String, block: () -> Unit) {
        tasks += Task(name, block)
    }

    fun runAll() {
        tasks.forEach { task ->
            println("Running task: ${task.name}")
            task.run()
        }
    }
}


