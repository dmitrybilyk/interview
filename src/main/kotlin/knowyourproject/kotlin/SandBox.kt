package knowyourproject.kotlin

fun main() {
    val l = { param: String -> println("Lambda $param") }
    l("done")

    val m5 = multiplier(5)
    val m7 = multiplier(7)

    println(m5(m7(2)))

    withLambdaParam { param -> param * 2 }

    val task = Task("task1") { param -> println("task $param is executing") }
    task.printTaskName2(7)
//    task.apply { name = "ddd" }
    println(Task("111", { param -> println(param)}).apply { name = "ddd" })

    val lr: Task.(param: Int) -> Unit = { param -> name = "ddd$param" }
    task.lr(777)
    println(task.name)}

fun multiplier(param: Int): (param2: Int) -> Int {
    return { param2 -> param * param2 }
}

fun withLambdaParam( block: (Int) -> Int) {
    println(block(5))
}

data class Task(var name: String, val block: (param: Int) -> Unit) {}

fun Task.printTaskName2(param: Int) {
    println(this.name + this.name+param)
}

