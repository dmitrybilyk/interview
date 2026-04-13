package knowyourproject.reactive.sinks.callbacks

import reactor.core.publisher.Mono
import reactor.core.publisher.Sinks
import reactor.core.scheduler.Schedulers

fun main() {
    log("Starting request")

    val paymentService = ExternalPaymentService()

    payNonReactiveWithCallback(paymentService)

    payReactive(paymentService, 100)
        .doOnNext { log("Reactive pipeline received result") }
        .publishOn(Schedulers.boundedElastic())
        .subscribe { result ->
            when (result) {
                is PaymentResult.Success ->
                    log("✅ Save payment ${result.paymentId} to DB")

                is PaymentResult.Failure ->
                    log("❌ Handle error ${result.error.message}")
            }
        }

    log("HTTP thread released immediately")

    log("Main thread is free and continues")
}

private fun payNonReactiveWithCallback(paymentService: ExternalPaymentService) {
    paymentService.pay(
        amount = 100,
        callback = object : PaymentCallback {

            override fun onSuccess(paymentId: String) {
                log("SUCCESS callback received: $paymentId")
                saveToDatabase(paymentId)
            }

            private fun saveToDatabase(paymentId: String) {
                println("saving to db $paymentId")
            }

            override fun onFailure(error: Throwable) {
                log("FAILURE callback received: ${error.message}")
            }
        }
    )
}

fun payReactive(
    service: ExternalPaymentService,
    amount: Int
): Mono<PaymentResult> {

    val sink = Sinks.one<PaymentResult>()

    service.pay(
        amount,
        object : PaymentCallback {

            override fun onSuccess(paymentId: String) {
                log("Callback SUCCESS")
                sink.tryEmitValue(PaymentResult.Success(paymentId))
            }

            override fun onFailure(error: Throwable) {
                log("Callback FAILURE")
                sink.tryEmitValue(PaymentResult.Failure(error))
            }
        }
    )

    return sink.asMono()
}



fun log(msg: String) {
    println("[${Thread.currentThread().name}] $msg")
}

