package knowyourproject.reactive.sinks

import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import java.time.Duration

fun main() {

    val b = true

    val monoSink = getResult()
    val mono = monoSink.asMono()
//    val mono2 = monoSink.asMono()

//    if (b) {
        mono.subscribe{ println("$it in mono1") }
        mono.subscribe{ println("$it in mono2") }
//    } else {
//        mono2.subscribe{ println("$it in mono2") }
//    }
//    monoSink.tryEmitValue("Dmytro2")

    callEnrich("Dmytro", object: EnrichmentCallback {
        override fun onSuccess() {
            println("doing after success")
        }

        override fun onFailure() {
            println("doing after failure")
        }

    })

    callEnrichWithSink("Dmytro")
//        .timeout(Duration.ofSeconds(1))
        .subscribe { println("goooood") }

    Thread.sleep(1000)
}

fun getResult(): Sinks.One<String> {
    val s = Sinks.one<String>()
    s.tryEmitValue("some result")
    return s
}



interface EnrichmentCallback {
    fun onSuccess()
    fun onFailure()
}

fun callEnrich(param: String, callback: EnrichmentCallback) {
    val result = param + "enriched"
    val isSuccess = true
    if (isSuccess) {
        callback.onSuccess()
    } else {
        callback.onFailure()
    }
}

fun callEnrichWithSink(param: String): Mono<String> {
    val sink = Sinks.one<String>()
    Thread {
        try {
            println("Enriching on thread: ${Thread.currentThread().name}")
            Thread.sleep(2000)
            sink.tryEmitValue("$param enriched")
        } catch (e: Exception) {
            sink.tryEmitError(e)
        }
    }.start()
    return sink.asMono()
}