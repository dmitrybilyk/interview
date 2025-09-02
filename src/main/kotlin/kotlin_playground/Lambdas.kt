package kotlin_playground

fun main() {

    val employee = listOf(
        Employee("ddd222", "gfff22", 222),
        Employee("ddd11", "gfff11", 111),
        Employee("ddd333", "gfff33", 333),
    )

    println(employee.minBy(Employee::startYear))

//    var num = 10
//
//    run {
//         num += 15
//        println(num)
//    }

//    run(::topLevel)

    println(countTo100())
}

//fun countTo100() =
//     with(StringBuilder()) {
//        for (i in 1..99) {
//            append(i)
//            append(", ")
//        }
//        append(100)
//        toString()
//}

fun countTo100() =
    StringBuilder().apply {
        for (i in 1..99) {
            append(i)
            append(", ")
        }
        append(100)
    }.toString()

fun topLevel() = println("Im in a function")

fun useParameter(employees: List<Employee>, num: Int) {
    employees.forEach {
        println(it.firstName)
        println(num)
    }
}

data class Employee(val firstName: String, val lastName: String, val startYear: Int)