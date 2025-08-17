fun main() {
//    val people = listOf(GoodPerson("Alice", 29), GoodPerson("Bob", 31))
//    println(people.maxByOrNull( { person: GoodPerson -> person.age}))
//
//    val sum = {a: Int, b: Int -> a + b}
//    println(sum(5, 6))
//
//    println(run { sum(4, 3) })
//
//    val names = people.joinToString(" ") { p: GoodPerson -> p.name }
//    println(names)

//    val multiplyLambda = ::multipy
//    print(multiplyLambda(10))
    val seb = GoodPerson("Sebastian", 26)
    val personsAgeFunction = GoodPerson::age
    println(personsAgeFunction(seb))
    // 26
    val sebsAgeFunction = seb::age
    println(sebsAgeFunction())
    // 26

}

fun multipy(a: Int): Int {
    return a * a
}

data class GoodPerson(val name: String, val age: Int) {
    fun multiply(a: Int): Int {
        return a * a
    }
}

//fun handleComputation(id: String) {
//    postponeComputation(1000) {       ❶
//        println(id)                   ❷
//    }
//}