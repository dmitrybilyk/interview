package kotlin_playground

fun main() {
    var regularPaper = Box<Regular>()
    var paper = Box<Paper>()
//    paper = regularPaper

    regularPaper = paper
}

class Box<in T> {
//    fun takePaper(): T {
//
//    }

    fun addPaper(paper: T) {

    }
}

open class Paper

class Regular: Paper()

class Premium: Paper()