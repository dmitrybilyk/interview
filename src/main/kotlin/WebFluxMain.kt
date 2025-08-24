import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import java.lang.Thread.sleep
import java.time.Duration
import java.time.Instant
import kotlin.system.measureNanoTime
import kotlin.time.measureTime

fun main() {

    val startTime = Instant.now()

    val externalServiceResult = Mono.fromCallable { externalCall() }
//        .subscribeOn(Schedulers.parallel())

    val dbResult = Mono.fromCallable { dbCall() }
//        .subscribeOn(Schedulers.parallel())

    externalServiceResult.mergeWith(dbResult)
//        .doOnNext { println(it) }  // print the result
        .flatMap { Mono.just(it) }
        .doFinally {
            val elapsedMs = Duration.between(startTime, Instant.now()).toMillis()
            println("Elapsed time: $elapsedMs ms")
        }
        .subscribe()

    sleep(3000)  // keep JVM alive

}

fun dbCall(): String {
    println("Executing db call")
    sleep(2000)
    return "result from db"
}

fun externalCall(): String {
    println("Executing external service")
    sleep(2000)
    return "result from external service"
}