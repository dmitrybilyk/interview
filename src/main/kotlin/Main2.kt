fun main() {
    val bird = Bird("Sokol")
    bird.fly()

    val smallBird = SmallBird()
    smallBird.fly()

    val parentSealed: Parent = Child2()

    when (parentSealed) {
        is Child1 -> "child1"
        is Child2 -> "child2"
        is Child3 -> "child3"
    }

}

interface Flyable {
    val flyHeight: Int
    fun fly()
    fun goUp() {
        println("I'm going up to the height - $flyHeight")
    }
}

open class Bird(val name: String): Flyable {
    override val flyHeight: Int
        get() = 500

    override fun fly() {
        println(" I'm a $name and I can fly ")
        goUp()
    }
}

class SmallBird: Bird("Small Bird") {
    override val flyHeight: Int
        get() = 200

    override fun goUp() {
        println(" I'm a small bird and I'm going to height - $flyHeight")
    }
}

sealed class Parent(val state: String) {
    fun returnResult() {
        println("some parent result")
    }
}

class Child1: Parent("child1")
class Child2: Parent("child2")
class Child3: Parent("child3")