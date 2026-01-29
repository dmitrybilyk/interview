package knowyourproject.reactive.sinks.callbacks

class ExternalPaymentService {

    fun pay(amount: Int, callback: PaymentCallback) {
        log("Received payment request: $amount")

        Thread {
            log("Processing payment in provider")

            try {
                Thread.sleep(1000)

                if (amount > 0) {
                    log("Payment approved")
                    callback.onSuccess("PAY-${System.currentTimeMillis()}")
                } else {
                    throw IllegalArgumentException("Invalid amount")
                }
            } catch (e: Exception) {
                callback.onFailure(e)
            }
        }.apply {
            name = "payment-provider-thread"
            start()
        }
    }
}
