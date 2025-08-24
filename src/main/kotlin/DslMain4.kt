//fun main() {
////    val shoppingList = ShoppingList()
////    shoppingList.item(Item("Milk", 7))
////    shoppingList.item(Item("Bread", 3))
////    shoppingList.item(Item("Candy", 2))
////
////    shoppingList.printAllItems()
//
//    val shoppingListDsl = shoppingList {
//        item(Item("Milk", 7))
//        item(Item("Bread", 3))
//        item(Item("Candy", 2))
//    }
//
//    shoppingListDsl.printAllItems()
//
//}
//
//fun shoppingList(block: ShoppingList.() -> Unit): ShoppingList {
//    val shoppingList = ShoppingList()
//    shoppingList.block()
//    return shoppingList
//}
//
//class ShoppingList {
//    private val items = mutableListOf<Item>()
//
//    fun item(item: Item) {
//        items.add(item)
//    }
//
//    fun printAllItems() {
//        items.forEach { println("Name - ${it.name}, quantity - ${it.quantity}") }
//    }
//}
//
//data class Item(val name: String, val quantity: Int)