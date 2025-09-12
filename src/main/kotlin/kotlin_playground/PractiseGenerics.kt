package kotlin_playground


fun main() {
    val joe = Person("Joe", "Jones", 45)
    val jane = Person("Jane", "Smith", 12)
    val mary = Person("Mary", "Wilson", 70)
    val john = Person("John", "Adams", 32)
    val jean = Person("Jean", "Smithson", 66)

    val map: Map<String, Person> = listOf(joe, jane, mary, john, jean)
        .associateBy { it.lastName }

    val pairsList = map.map { Pair(it.value.firstName, it.value.lastName)}

    val firstNamesList = map.map{ it.value.firstName}
    val lastNamesList = map.map{ it.value.lastName}

    val pairs = firstNamesList.zip(lastNamesList)

    map.values.also {
        println("$it.")
    }

    val anotherMap = mapOf(
        joe.lastName to joe,
        jane.lastName to jane,
        mary.lastName to mary,
        john.lastName to john,
        jean.lastName to jean).count { it.key.startsWith('s', true) }

    val (fName, lName, age) = joe
    println("fname = $fName, lName = $lName, age = $age")
}

class Person(val firstName: String, val lastName: String, val age: Int) {
    operator fun component1() = firstName
    operator fun component2() = lastName
    operator fun component3() = age
}