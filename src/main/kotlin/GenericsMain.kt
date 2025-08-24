fun main() {
//    val processor = Processor<Int>()
//    processor.process(5)
//
//    val subProcessor = SubProcessor<Long>()
//    subProcessor.process(50L)
////    processor.process("fff")
//    val pair = PairUtils.swap(Pair("a", "b"))
//    println(pair)
//
//    printContents(listOf("abc", "bac"))
//
//    val strings = mutableListOf("abc", "bac")
////    addAnswer(strings)
//    println(strings.maxBy { it.length })


//    val herd = listOf(Cat("cat1"), Cat("cat2"), Cat("cat3"))

//    feedAll(herd)

//    val weightComparator = Comparator<Fruit> { a, b ->
//        a.weight - b.weight
//    }
//    val fruits: List<Fruit> = listOf(
//        Orange(180, true),
//        Apple(100, "green")
//    )
//    val apples: List<Apple> = listOf(
//        Apple(50, "red"),
//        Apple(120, "green"),
//        Apple(155, "yellow")
//    )
//    println(fruits.sortedWith(weightComparator))
//    // [Apple(weight=100, color=green), Orange(weight=180, juicy=true)]
//    println(apples.sortedWith(weightComparator))
//    // [Apple(weight=50, color=red), Apple(weight=120, color=green),
////    Apple(weight=155, color=yellow)]
//
//    acceptProducer(StringProducer())
//
//    val animals: MutableList<Animal> = mutableListOf()
//    val cats: List<Cat> = listOf(Cat(), Cat())
//    copyElements(cats, animals)

//    val dogs: List<Dog> = mutableListOf(Dog("Rex"), Dog("Buddy"))
//    val animals: MutableList<Animal> = mutableListOf(Animal("Generic"))
//
//    val cageAnimals = Cage(animals)
//    val cageDogs = Cage(dogs)
//
//    feedAll(cageAnimals)   // ❌ This won’t compile
//    feedAll(cageDogs)   // ❌ This won’t compile
//    println(animals)

    val dogHouse = DogHouse<Animal>()
    dogHouse.addAnimal(Animal("animal name"))
    dogHouse.addAnimal(Dog("dog name"))
}

class DogHouse<T: Animal> {
    private val animalsList = mutableListOf<T>()
    fun addAnimal(animal: T) {
        animalsList.add(animal)
    }
}

open class Animal(val name: String)
class Dog(name: String): Animal(name)

//fun feedAll(cage: Cage<out Animal>) {
//    for ((idx, _) in log.withIndex()) {
//        println("Feeding ${cage.getAnimal(idx)}")
//    }
//}
//
//class Cage<T : Animal>(private val animals: List<T>) {
//    fun getAnimal(index: Int): T = animals[index]
//    val size: Int get() = animals.size
//}
//
//open class Animal(val name: String) {
//    open fun makeSound() = println("$name makes some sound")
//}
//
//class Dog(name: String) : Animal(name) {
//    override fun makeSound() = println("$name: Woof!")
//}

//fun <T: Animal> copyElements(cats: List<out T>, animals: MutableList<in T>) {
//    animals.addAll(cats)
//}
//
//interface Consumer<in T> {
//    fun consume(item: T)
//}
//
//
//interface Producer<out T> {
//    fun produce(): T
//}
//
//class StringProducer: Producer<String> {
//    override fun produce(): String {
//        return "Hello"
//    }
//}
//
//fun acceptProducer(producer: Producer<Any>) {
//    inProducer(producer)
//}
//
//fun inProducer(producer: Producer<Any>) {
//    println(producer)
//}
//
//sealed class Fruit {
//    abstract val weight: Int
//}
//
//data class Apple(
//    override val weight: Int,
//    val color: String,
//): Fruit()
//
//data class Orange(
//    override val weight: Int,
//    val juicy: Boolean,
//): Fruit()
//
//
//// Base class
//open class Animal() {
//
//}
//
//// Subclass
//class Cat() : Animal() {
//
//}
//
//// Covariant Herd class
//class Herd<out T : Animal>(private val animals: List<T>) {
//    val size: Int get() = animals.size
//
//    operator fun get(i: Int): T = animals[i]
//}
//
////// Function to feed all animals
////fun feedAll(animals: Herd<Animal>) {
////    for (i in 0 until animals.size) {
////        animals[i].feed()
////    }
////}
////
////// Function to take care of cats
////fun takeCareOfCats(cats: Herd<Cat>) {
////    for (i in 0 until cats.size) {
////        cats[i].cleanLitter()
////    }
////    // Now we can pass cats to feedAll because Herd<T> is covariant
////    feedAll(cats)
////}
//
//fun printContents(list: List<Any>) {
//    println(list.joinToString())
//}
//
//fun addAnswer(list: MutableList<Any>) {
//    list.add(42)
//}
//
//fun <T> isString(value: T): Boolean {
//    return value is T  // ❌ This won't compile!
//}
//
//class Repository<T: Identifiable<ID>, ID: Comparable<ID>> {
//    private val map = mutableMapOf<ID, T>()
//    fun save(item: T) {
//        map[item.id] = item
//    }
//    fun findById(id: ID): T? {
//        return map[id]
//    }
//    fun delete(id: ID): Boolean {
//        return map.remove(id) != null
//    }
//    fun all(): List<T> {
//        return map.values.toList()
//    }
//}
//
//interface Identifiable<ID> {
//    val id: ID
//}
//
//@Suppress("UNCHECKED_CAST")
//class Calculator<T: Number> {
//    fun add(a: T, b: T): Double = a.toDouble() + b.toDouble()
//    fun multiply(a: T, b: T): Double = a.toDouble() * b.toDouble()
//    fun average(list: List<T>): T = list.map { it.toDouble() }.average() as T
//}
//
//
//fun <T, R> Pair<T, R>.toReversedPair(): Pair<R, T> {
//    return Pair(this.second, this.first)
//}
//
//object PairUtils {
//    fun <T, R> swap(pair: Pair<T, R>): Pair<R, T> {
//        val (t, r) = pair
//        val newPar = Pair(r, t)
//        return newPar
//    }
//}
//
//class Stack<T> {
//    private val storage: MutableList<T> = mutableListOf()
//
//    fun push(t:T) {
//        storage.addLast(t)
//    }
//    fun pop(): T {
//        val last = storage.last()
//        storage.removeLast()
//        return last
//    }
//    fun peek(): T {
//        return storage.last()
//    }
//    fun isEmpty(): Boolean {
//        return storage.isEmpty()
//    }
//}
//
////class Box<T>(var value: T) {
////    fun getValue(): T {
////        return value
////    }
////
////    fun setValue(t: T) {
////        this.value = t
////    }
////}
//
//fun<T> swap(list: MutableList<T>, first: Int, second: Int) {
//    val temp: T = list[first]
//    list[first] = list[second]
//    list[second] = temp
//}
//
//fun <T: Comparable<T>> maxOfTwo(first: T, second: T): T {
//    return if (first > second) {
//        first
//    } else {
//        second
//    }
//}
//
//data class Human(val name: String): Comparable<Human> {
//    override fun compareTo(other: Human): Int {
//        return this.name.compareTo(other.name)
//    }
//}
//
//open class Processor<T: Number> {
//    open fun process(value: T) {
//        println("Im' processing - $value")
//    }
//}
//
//class SubProcessor<T: Number>: Processor<T>(){
//    override fun process(value: T) {
//        println("Im subprocessing - $value")
//    }
//}