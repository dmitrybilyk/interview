package knowyourproject.reactive.sinks.callbacks

interface PaymentCallback {
    fun onSuccess(paymentId: String)
    fun onFailure(error: Throwable)
}
