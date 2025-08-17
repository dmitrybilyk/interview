import java.io.File

fun main() {
//    println("check")
//
//    val person = Person("Dmytro")
//
//    val child = Child("Mykyta", "pc")
//    val grandChild = GrandChild("Mykyta's son", "real toy")
//
//    val list = listOf(child, grandChild, person)
//
//    list.forEach { it.printNameUppercase() }
//
//    printSpecifics(list)
//
//    val context = Context()
//    context.someMethod(true)
//
//    joinToString(list, ", ", "pre", "fff")
//
//    val stringList = listOf("fdf", "fdf")
//
//    withVarargs(*stringList.toTypedArray())
//
//    1 to "one"
//
//    val files = listOf(File("/Z"), File("/a"))
//    println(files.sortedWith(CaseInsensitiveFileComparator))
}

//class User {
//    val nickname: String
//
//    constructor(email: String) {                ❶
//        nickname = email.substringBefore('@')
//    }
//
//    constructor(socialAccountId: Int) {         ❶
//        nickname = getSocialNetworkName(socialAccountId)
//    }
//}



data class Person(val name: String) {
    object NameComparator : Comparator<Person> {
        override fun compare(p1: Person, p2: Person): Int =
            p1.name.compareTo(p2.name)
    }
}


object CaseInsensitiveFileComparator : Comparator<File> {
    override fun compare(file1: File, file2: File): Int {
        return file1.path.compareTo(file2.path,
            ignoreCase = true)
    }
}

class CountingSet<T>(
    private val innerSet: MutableCollection<T> = hashSetOf<T>()
) : MutableCollection<T> by innerSet {

    private var objectsAdded = 0

    override fun add(element: T): Boolean {
        objectsAdded++
        return innerSet.add(element)
    }

    override fun addAll(elements: Collection<T>): Boolean {
        objectsAdded += elements.size
        return innerSet.addAll(elements)
    }
}

fun withVarargs(vararg params: String ) {
    params.forEach { println(it) }
}

fun <T> joinToString(
    collection: Collection<T>,
    separator: String,
    prefix: String,
    postfix: String
): String {

    val result = StringBuilder(prefix)

    for ((index, element) in collection.withIndex()) {
        if (index > 0) result.append(separator)
        result.append(element)
    }

    result.append(postfix)
    return result.toString()
}

fun Person.printNameUppercase() {
    println(this.name.uppercase())
}

//fun printSpecifics(list: List<Person>) {
//    list.forEach {
//        when (it) {
//            is Child -> println(it.toy)
//            is GrandChild -> println(it.small)
//            else -> println("just name ${it.name}")
//        }
//    }
//
//}

class Context {
    fun someMethod(flag: Boolean) {
        if (flag) {
            throw MyException("Error occurred. Please check.", 5)
        }
    }
}

//open class Person(open val name: String) {
//    open fun getDoubleName() = name + name
//}
//
//data class Child(
//    override var name: String,
//    val toy: String): Person(name) {
//    override fun getDoubleName(): String {
//        return super.getDoubleName() + " " + toy
//    }
//}
//
//data class GrandChild(
//    override var name: String,
//    val small: String): Person(name) {
//    override fun getDoubleName(): String {
//        return super.getDoubleName() + " " + small
//    }
//}

enum class HearColor {
    BLACK,
    WHITE,
    RED
}

enum class SportActivity(var level: Int) {
    LAZY(1),
    ACTIVE(5),
    SUPER_ACTIVE(10);

    fun printActivity(sportActivity: SportActivity) {
        println("The level of activity is ${sportActivity.level}")
    }
}

class MyException(override val message:String?, val level: Int): Exception(message) {

}