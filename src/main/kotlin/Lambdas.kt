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

    val result = with (GoodPerson("Dmytro", 44)) {
        name = "Dmytro2"
        age = 35
        true
    }

    val listStrings = listOf("dfd", "gfgf", "erer")
    println(createStringFromList(listStrings))

    val betterPerson = BetterPerson(1).apply {
        name = "Better name"
        age = 33
        blond = true
        println(" The name is $name")
    }

    val oneMoreBetterPerson = BetterPerson(2).also {
        it.name = "one more better"
        println(it.name)
    }

    println(betterPerson)

    println(result)

}




fun createStringFromList(list: List<String>): String {
    return buildString {
        for (element in list) {
            append(" ")
            append(element)
        }
    }
}

fun multipy(a: Int): Int {
    return a * a
}

data class GoodPerson(var name: String, var age: Int) {

    fun multiply(a: Int): Int {
        return a * a
    }
}

data class BetterPerson(val id: Int) {
    var name: String? = null
    var age: Int? = 0
    var blond: Boolean? = false
}

//fun handleComputation(id: String) {
//    postponeComputation(1000) {       ❶
//        println(id)                   ❷
//    }
//}