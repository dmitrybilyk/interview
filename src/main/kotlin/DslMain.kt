class Pizza {
    private val toppings = mutableListOf<String>()

    fun topping(name: String) {
        toppings.add(name)
    }

    override fun toString() = "Pizza with $toppings"
}

//fun pizza(block: Pizza.() -> Unit): Pizza {
//    val pizza = Pizza()
//    pizza.block()  // apply the builder block
//    return pizza
//}

fun menu(block: Menu.() -> Unit): Menu {
    return Menu().apply(block)
}

class Menu {
    private val pizzas = mutableListOf<Pizza>()

    fun pizza(block: Pizza.() -> Unit) {
        pizzas += Pizza().apply(block)
    }

    override fun toString() = pizzas.joinToString("\n")
}


fun main() {
    val myMenu = menu {
        pizza {
            topping("cheese")
        }
        pizza {
            topping("veggies")
            topping("olives")
        }
    }

    println(myMenu)
}

