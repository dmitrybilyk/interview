package knowyourproject.kotlin.extensionProperties

val String.wordsCount: Int
    get() = this.split(" ").size

fun main() {
    val text = "Hello Kotlin world"
    println(text.wordsCount)  // 3

    val sb = StringBuilder("Hello")
    println(sb.lastChar)  // 'o'

    sb.lastChar = '!'
    println(sb)
}

var StringBuilder.lastChar: Char
    get() = this[this.length - 1]
    set(value) { this.setCharAt(this.length - 1, value) }