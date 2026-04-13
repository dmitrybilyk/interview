package knowyourproject.kotlin.dsl

fun main() {
    val box = box("initialContent") {
        smallBox("smallContent") {}
        smallBox("smallContent2") {}
    }
    val box2 = box("initialContent2") {
        smallBox("smallContent2") {}
        smallBox("smallContent22") {}
    }

    println(box)

    Car().apply {
        name = "newName"
        model = "newModel"
    }
}

fun box(content:String, block: Box.() -> Unit): Box {
    return Box(content).apply(block)
}

data class Car(var name: String = "def", var model: String = "def2")

fun Car.handle(block: Car.() -> Unit) {
    block()
}

data class Box(val content: String) {
    private val smallBoxesList = mutableListOf<SmallBox>()

    fun smallBox(content: String, block: SmallBox.() -> Unit) {
        val smallBox = SmallBox(content).apply(block)
        smallBoxesList += smallBox
    }
}

data class SmallBox(val content: String) {

}
