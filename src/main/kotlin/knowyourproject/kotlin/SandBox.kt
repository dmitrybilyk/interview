package knowyourproject.kotlin

class Box(val value: Any)

fun test(box: Box) {
    if (box.value is String) {
        println(box.value.length) // ❌ not smart-cast
    }
}

fun main() {

    checkDefaultValues(7)

    val a = null
    val b = 5

    println(a?:b)

    var x: Any = "hello"
//    x = 4

    if (x is String) {
        println(x.length) // ❌ compile error
    }



//    val l = { param: String -> println("Lambda $param") }
//    l("done")
//
//    val m5 = multiplier(5)
//    val m7 = multiplier(7)
//
//    println(m5(m7(2)))
//
//    withLambdaParam { param -> param * 2 }
//
//    val task = Task("task1") { param -> println("task $param is executing") }
//    task.printTaskName2(7)
////    task.apply { name = "ddd" }
//    println(Task("111", { param -> println(param)}).apply { name = "ddd" })
//
//    val lr: Task.(param: Int) -> Unit = { param -> name = "ddd$param" }
//    task.lr(777)
//    println(task.name)
}

fun checkDefaultValues(p: Int, param1: String = "default", param2: Int = 0) {
    println("Default value used: $param1")
}

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

