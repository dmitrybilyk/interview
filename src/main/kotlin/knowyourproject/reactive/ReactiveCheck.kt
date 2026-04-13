package knowyourproject.reactive

import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.time.Duration


fun main() {
    val monoMovie = Mono.defer {
        println("Created on ${Thread.currentThread().name}")
        Mono.just(Movie(randomString(12), 1999))
    }

    monoMovie
        .doOnSubscribe { println("Subscribed on ${Thread.currentThread().name}") }
        .delayElement(Duration.ofSeconds(1))
        .doOnNext { println("After delay on ${Thread.currentThread().name}") }
        .publishOn(Schedulers.boundedElastic())
        .doOnNext { println("After publishOn on ${Thread.currentThread().name}") }
        .subscribe {
            println("Subscriber on ${Thread.currentThread().name}")
        }

    Thread.sleep(10000)

}

fun randomString(length: Int = 10): String {
    val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    return (1..length)
        .map { chars.random() }
        .joinToString("")
}

data class Movie(val name: String, val year: Int)