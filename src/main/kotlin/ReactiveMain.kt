import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep
import java.time.Duration

fun main() {

    getBookTitles()
        .flatMap { title ->
            getAuthor(title)
                .map { Book(title, it) }
        }
        .collectList()
        .subscribe{ println(it) }
}

data class Book(val title: String, val author: String)

fun getBookTitles(): Flux<String> = Flux.just("1984", "Brave New World", "Fahrenheit 451")

fun getAuthor(title: String): Mono<String> =
    when(title) {
        "1984" -> Mono.just("George Orwell")
        "Brave New World" -> Mono.just("Aldous Huxley")
        "Fahrenheit 451" -> Mono.just("Ray Bradbury")
        else -> Mono.empty()
    }
