package knowyourproject.kotlin.dsl

fun main() {
    val building = building {
        number = 1
        flat {
            number = 1
            room {
                name = "Bedroom"
                area = 20
            }
            room {
                name = "Bathroom"
                area = 10
            }
        }
    }

    building.show()
}

@BuildingDsl
class Room {
    var name: String? = null
    var area: Int? = 0
}

@BuildingDsl
class Flat {
    var number: Int? = null
    var rooms: List<Room> = mutableListOf()

    fun room(block: Room.() -> Unit): Room {
        return Room().apply(block)
    }
}

@BuildingDsl
class Building {
    var number: Int? = null
    var flats: List<Flat> = mutableListOf()

    fun flat(block: Flat.() -> Unit): Flat {
        return Flat().apply(block)
    }

    fun show() {
        flats.forEach { flat ->
            println("Flat number: ${flat.number}")
            flat.rooms.forEach { room ->
                println("Room name: ${room.name}, area: ${room.area}")
            }
        }
    }
}

fun building(block: Building.() -> Unit): Building {
    return Building().apply(block)
}

@DslMarker
annotation class BuildingDsl()