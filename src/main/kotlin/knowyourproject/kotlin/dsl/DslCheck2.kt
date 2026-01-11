//package knowyourproject.kotlin.dsl
//
////Building>flat>Room
//class DslCheck2 {
//
//}
//
//class Room {
//    val name: String
//    val area: Int
//
//    constructor(name: String, area: Int) {
//        this.name = name
//        this.area = area
//    }
//}
//
//class Flat {
//    val number: Int
//
//    val rooms: List<Room>
//
//    constructor(rooms: List<Room>, number: Int) {
//        this.rooms = rooms
//        this.number = number
//    }
//}
//
//class Building {
//    val number: Int
//    val flats: List<Flat>
//
//    constructor(flats: List<Flat>, number: Int) {
//        this.flats = flats
//        this.number = number
//    }
//
//}
//
//fun building(number: Int, block: Building.() -> Unit): Building {
//    return Building(number).block()
//}