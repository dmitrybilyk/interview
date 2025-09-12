package kotlin_playground

fun main() {
//    val dogs: MutableList<Dog> = mutableListOf(Dog())
//    val animals: MutableList<Animal> = dogs

    val dogs = mutableListOf(Dog(), Dog())
    val animals = mutableListOf(Dog(), Dog())

    val students = mutableListOf(Student(), Student())

    someMethod(dogs)

//    val producer: Producer<Animal> = DogProducer()
//    val animal: Animal = producer.produce()

    val consumer: Consumer<Dog> = DogConsumer()
}

fun someMethod(list: MutableList<Dog>) {
    println(list.first())
    list.add(Dog())
}

class Student

open class Animal
class Dog : Animal()
class Cat : Animal()

interface Producer<out T> {
    fun produce(): T
//    fun consume(t: T)
}

interface Consumer<T> {
//    fun produce(): T
    fun consume(t: T)
}

class DogProducer : Producer<Dog> {
    override fun produce() = Dog()
}

class DogConsumer : Consumer<Dog> {
    override fun consume(t: Dog) {
        println(t)
    }

}

