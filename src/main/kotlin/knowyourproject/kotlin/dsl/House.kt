package knowyourproject.kotlin.dsl

@DslMarker
annotation class HouseDsl

@HouseDsl
class House {
    var name: String = "Default House"
    private val furnitureList = mutableListOf<Furniture>()

    fun furniture(block: Furniture.() -> Unit) {
        val item = Furniture().apply(block)
        furnitureList += item
    }

    fun show() {
        println("🏠 $name")
        furnitureList.forEach {
            println("   🪑 ${it.name}")
        }
    }
}

@HouseDsl
class Furniture {
    var name: String = "Unnamed furniture"
}

fun house(block: House.() -> Unit): House {
    return House().apply(block)
}

fun main() {
    val myHouse = house {
        name = "Dream House"

        furniture {
            name = "Table"
        }

        furniture {
            name = "Chair"
        }

        // Without @DslMarker you could wrongly do:
        // name = "Oops"  // changes house name even inside furniture block
    }

    myHouse.show()
}
