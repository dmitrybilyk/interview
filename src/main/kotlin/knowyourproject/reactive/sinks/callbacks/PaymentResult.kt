package knowyourproject.reactive.sinks.callbacks

sealed class PaymentResult {

    data class Success(val paymentId: String) : PaymentResult()
    
    data class Failure(val error: Throwable) : PaymentResult()
}
