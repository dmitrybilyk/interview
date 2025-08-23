import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep
import java.time.Duration

fun main() {

    val aaplFlux = stockFeed("AAPL")
    val googFlux = stockFeed("GOOG")

    Flux.merge(aaplFlux, googFlux)
        .log()
        .filter { it.price > 150 }
        .take(5)
        .sample(Duration.ofMillis(500))
        .buffer(3)
        .subscribeOn(Schedulers.parallel())
        .subscribe { println(it) }

    sleep(5000)
}

data class StockPrice(val symbol: String, val price: Double)

fun stockFeed(symbol: String): Flux<StockPrice> =
    Flux.interval(Duration.ofMillis(300))
        .map { StockPrice(symbol, (100..200).random().toDouble()) }
        .take(10) // simulate 10 updates
